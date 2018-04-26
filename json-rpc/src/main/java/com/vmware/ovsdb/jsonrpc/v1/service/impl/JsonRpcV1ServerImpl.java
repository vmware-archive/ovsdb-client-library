/*
 * Copyright (c) 2018 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the
 * terms and conditions of the subcomponent's license, as noted in the LICENSE
 * file.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package com.vmware.ovsdb.jsonrpc.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.jsonrpc.v1.annotation.JsonRpcServiceMethod;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcConnectionClosedException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Response;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Server;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An implementation of {@link JsonRpcV1Server} interface. It depends on a request handler that
 * contains methods to handle each incoming request. Only methods in the handler that are annotated
 * with {@link JsonRpcServiceMethod} will be used to handle requests.
 *
 * <p>It depends on a {@link JsonRpcTransporter} to send the responses to the client. Whenever the
 * server receives an invalid request, the connection will be closed by {@link
 * JsonRpcTransporter#close()} and all following call will throw a {@link
 * JsonRpcConnectionClosedException}.
 *
 * <p>The implementation is thread-safe.
 */
public class JsonRpcV1ServerImpl implements JsonRpcV1Server {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());

  private final JsonRpcTransporter transporter;

  private final Object requestHandler;

  private final Map<String, Method> methods;

  private AtomicBoolean isActive = new AtomicBoolean(true);

  /**
   * Construct a JsonRpcV1ServerImpl obejct.
   *
   * @param transporter a {@link JsonRpcTransporter} used to send outgoing responses
   * @param requestHandler a thread-safe handler that contains methods to handle the RPC requests
   * @throws IllegalArgumentException if two methods in the handler have the same RPC method name
   */
  public JsonRpcV1ServerImpl(JsonRpcTransporter transporter, Object requestHandler) {
    this.transporter = transporter;
    this.requestHandler = requestHandler;
    methods = createMethodMap(requestHandler);
  }

  @Override
  public void handleRequest(JsonNode requestNode) throws JsonRpcException {
    throwExceptionIfNotActive();

    JsonRpcV1Request request;
    try {
      request = JsonUtil.treeToValue(requestNode, JsonRpcV1Request.class);
    } catch (JsonProcessingException ex) {
      LOGGER.error("Invalid request {}. Closing the server.", requestNode);
      shutdown();
      return;
    }
    String method = request.getMethod();
    String id = request.getId();
    Method methodHandle = methods.get(method);

    LOGGER.debug("Handling request {} using method {}", requestNode, methodHandle);
    if (methodHandle == null) {
      LOGGER.warn("Unknown method: {}", method);
      if (id != null) {
        sendResponse(id, null, "unknown method " + method);
      }
      return;
    }
    Object result = null;
    String error = null;
    try {
      Object[] params = toMethodParams(request.getParams(), methodHandle);
      LOGGER.info("Calling {}({})", method, params);
      result = methodHandle.invoke(requestHandler, params);
    } catch (Throwable ex) {
      LOGGER.info("Invocation of methods " + method + " throws exception", ex);
      error = ex.getCause() == null
          ? ex.getMessage()
          : ex.getCause().getMessage();
    }
    // Only response to client if this is not a notification
    if (id != null) {
      LOGGER.debug("Sending response of request {}", id);
      sendResponse(id, result, error);
    }
  }

  @Override
  public void shutdown() {
    if (isActive.getAndSet(false)) {
      transporter.close();
      LOGGER.info("The server is shutdown.");
    }
  }

  private void sendResponse(String id, Object result, String error) throws JsonRpcException {
    JsonRpcV1Response response = new JsonRpcV1Response(result, error, id);
    JsonNode responseNode = JsonUtil.toJsonNode(response);
    LOGGER.info("Sending response: {}", responseNode);
    transporter.send(responseNode);
  }

  private Object[] toMethodParams(ArrayNode paramsNode, Method method) {
    Parameter[] parameters = method.getParameters();
    int methodParamSize = parameters.length;
    int actualParamSize = paramsNode.size();

    Object[] actualParams = new Object[methodParamSize];
    if (methodParamSize != actualParamSize) {
      // This is not an error only if the last arg is an vararg
      // And the actual params number must be >= methods params number - 1
      // For e.g. if a methods has n params, the last one is an vararg,
      // then the number of actual params can be [n-1, ...)
      int lastIndex = methodParamSize - 1;
      if (lastIndex >= 0
          && parameters[lastIndex].isVarArgs()
          && actualParamSize >= lastIndex) {

        if (actualParamSize > lastIndex) {
          Class<?> type = parameters[lastIndex].getType().getComponentType();
          actualParams[lastIndex] = buildVarargParam(paramsNode, lastIndex, type);
        }
        // We have handled the last param, no need to handle it later
        --methodParamSize;
      } else {
        throw new IllegalArgumentException(
            "Parameters number doesn't match. Expected: "
                + methodParamSize + ". Got: " + paramsNode.size());
      }
    }

    for (int i = 0; i < methodParamSize; i++) {
      JsonNode paramNode = paramsNode.get(i);
      Class<?> type = parameters[i].getType();
      try {
        actualParams[i] = JsonUtil.treeToValue(paramNode, type);
      } catch (JsonProcessingException ex) {
        throw new IllegalArgumentException(
            "Failed to convert param " + paramNode + " to type " + type
        );
      }
    }

    return actualParams;
  }

  private Object buildVarargParam(
      ArrayNode paramsNode, int start, Class<?> type
  ) {
    int varArgParamSize = paramsNode.size() - start;
    Object varArgParam = Array.newInstance(type, varArgParamSize);

    for (int i = 0; i < varArgParamSize; i++) {
      JsonNode paramNode = paramsNode.get(start + i);
      try {
        Array.set(varArgParam, i, JsonUtil.treeToValue(paramNode, type));
      } catch (JsonProcessingException ex) {
        throw new IllegalArgumentException(
            "Failed to convert param " + paramNode + " to type "
                + type
        );
      }
    }
    return varArgParam;
  }

  private void throwExceptionIfNotActive() throws JsonRpcConnectionClosedException {
    if (!isActive.get()) {
      throw new JsonRpcConnectionClosedException("Connection for this server is closed.");
    }
  }

  private Map<String, Method> createMethodMap(Object requestHandler) {
    Map<String, Method> methodMap = new HashMap<>();
    Arrays.stream(requestHandler.getClass().getMethods())
        .filter(method -> method.getAnnotation(JsonRpcServiceMethod.class) != null)
        .forEach(method -> {
          // If the value is empty, then just use the method name as the RPC method name
          String rpcMethodName = method.getAnnotation(JsonRpcServiceMethod.class).value();
          rpcMethodName = rpcMethodName.isEmpty() ? method.getName() : rpcMethodName;

          if (!methodMap.containsKey(rpcMethodName)) {
            methodMap.put(rpcMethodName, method);
          } else {
            // If there are two methods with the same RPC method name, throw an exception
            throw new IllegalArgumentException(
                "Method " + method + " and method " + methodMap.get(rpcMethodName)
                    + " have the same RPC method name " + rpcMethodName + "!"
            );
          }
        });
    return methodMap;
  }
}
