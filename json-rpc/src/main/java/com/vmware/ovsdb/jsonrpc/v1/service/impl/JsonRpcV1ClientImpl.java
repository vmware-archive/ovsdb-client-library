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
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcConnectionClosedException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcDuplicateIdException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcInvalidResponseException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcResultTypeMismatchException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Response;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Client;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link JsonRpcV1Client} interface. It depends on a {@link
 * JsonRpcTransporter} to send the requests to the remote server. Whenever the client receives an
 * invalid response, it will close the connection by {@link JsonRpcTransporter#close()}. All
 * un-answered request will result in exception. After the connection is closed, the client cannot
 * be used anymore and all following request will result in {@link JsonRpcConnectionClosedException}.
 *
 * The implementation is thread-safe.
 */
public class JsonRpcV1ClientImpl implements JsonRpcV1Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());

    private static final long DEFAULT_MAX_TIMEOUT = 300;

    private static final TimeUnit DEFAULT_MAX_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final long maxTimeout;

    private final TimeUnit maxTimeoutUnit;

    private final JsonRpcTransporter transporter;

    private final ConcurrentMap<String, CallContext> callContexts
        = new ConcurrentHashMap<>();

    private final AtomicBoolean isActive = new AtomicBoolean(true);

    private final ScheduledExecutorService scheduler;

    /**
     * Construct a JsonRpcV1ClientImpl object. If the user calls {@link CompletableFuture#get()}  on
     * the {@link CompletableFuture} returned by {@link JsonRpcV1Client#call(String, String, Class,
     * Object...)}, it is guaranteed that the get() will be returned within maxTimeout. If the user
     * calls {@link CompletableFuture#get(long, TimeUnit)}, the timeout value should be less than or
     * equal to maxTimeout. Or it will still return within maxTimeout.
     *
     * @param transporter a {@link JsonRpcTransporter} used to send outgoing requests
     * @param scheduler a scheduler used to run any asynchronous task
     * @param maxTimeout maximum timeout of each call
     * @param maxTimeoutUnit the time unit of the maxTimeout parameter
     */
    public JsonRpcV1ClientImpl(
        JsonRpcTransporter transporter, ScheduledExecutorService scheduler,
        long maxTimeout, TimeUnit maxTimeoutUnit
    ) {
        this.transporter = transporter;
        this.scheduler = scheduler;
        this.maxTimeout = maxTimeout;
        this.maxTimeoutUnit = maxTimeoutUnit;
    }

    /**
     * Construct a JsonRpcV1ClientImpl object. The default maximum timeout for each call will be
     * used. To specify the timeout, see {@link JsonRpcV1ClientImpl#JsonRpcV1ClientImpl(JsonRpcTransporter,
     * ScheduledExecutorService, long, TimeUnit)}.
     *
     * @param transporter a {@link JsonRpcTransporter} used to send outgoing requests.
     * @param scheduler a scheduler used to run any asynchronous task
     */
    public JsonRpcV1ClientImpl(JsonRpcTransporter transporter, ScheduledExecutorService scheduler) {
        this(transporter, scheduler, DEFAULT_MAX_TIMEOUT, DEFAULT_MAX_TIMEOUT_UNIT);
    }

    @Override
    public <T> CompletableFuture<T> call(
        String id, String method, Class<T> returnType, Object... params
    ) throws JsonRpcException {
        throwExceptionIfNotActive();
        JsonNode request = JsonUtil.toJsonNode(new JsonRpcV1Request(id, method, params));

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        CallContext<T> callContext = new CallContext<>(completableFuture, returnType);
        if (callContexts.putIfAbsent(id, callContext) != null) {
            LOGGER.error("Duplicate call id {} in request {}", id, request);
            throw new JsonRpcDuplicateIdException("Duplicate call id " + id);
        }

        // TODO: After upgrade to Java 9, change this to
        // completableFuture.orTimeout(maxTimeout, maxTimeoutUnit);
        // completableFuture.exceptionally(ex -> {
        //    if (ex instanceof TimeoutException) {
        //        callContexts.remove(id);
        //    }
        //    return null;
        // });
        ScheduledFuture timeoutFuture = scheduler.schedule(() -> {
            completableFuture.completeExceptionally(
                new TimeoutException("Timeout at " + System.currentTimeMillis()));
            callContexts.remove(id);
        }, maxTimeout, maxTimeoutUnit);

        callContext.setTimeoutFuture(timeoutFuture);
        try {
            sendRequest(request);
        } catch (JsonRpcTransportException e) {
            timeoutFuture.cancel(true);
            callContexts.remove(id);
            throw e;
        }

        return completableFuture;
    }

    @Override
    public void notify(String method, Object... params) throws JsonRpcException {
        throwExceptionIfNotActive();
        JsonNode request = JsonUtil.toJsonNode(new JsonRpcV1Request(null, method, params));
        sendRequest(request);
    }

    @Override
    public void handleResponse(JsonNode responseNode) throws JsonRpcException {
        throwExceptionIfNotActive();
        JsonRpcV1Response jsonRpcV1Response;
        try {
            jsonRpcV1Response = JsonUtil.treeToValue(responseNode, JsonRpcV1Response.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Invalid response {}. Closing the client.", responseNode);
            shutdown();
            throw new JsonRpcInvalidResponseException("Invalid response " + responseNode, e);
        }
        String id = jsonRpcV1Response.getId();
        if (id == null) {
            // Ignore response without ID
            LOGGER.warn("Response {} doesn't have an ID. Ignore.", jsonRpcV1Response);
            return;
        }
        CallContext callContext = callContexts.remove(id);
        if (callContext == null) {
            // Ignore response with unknown ID
            LOGGER.warn("Unknown response {}", jsonRpcV1Response);
            return;
        }
        // Cancel the timeout future since we have received the response
        callContext.getTimeoutFuture().cancel(true);
        CompletableFuture completableFuture = callContext.getCompletableFuture();

        String error = jsonRpcV1Response.getError();
        if (error != null) {
            completableFuture.completeExceptionally(new JsonRpcException(error));
        } else {
            JsonNode resultNode = jsonRpcV1Response.getResult();
            Class<?> returnType = callContext.getReturnType();
            try {
                Object result = JsonUtil.treeToValue(resultNode, returnType);
                completableFuture.complete(result);
            } catch (JsonProcessingException e) {
                completableFuture.completeExceptionally(
                    new JsonRpcResultTypeMismatchException(
                        "Failed to convert result " + resultNode + " to type " + returnType, e)
                );
            }
        }
    }

    @Override
    public void shutdown() {
        if (isActive.getAndSet(false)) {
            transporter.close();

            callContexts.forEach((key, callContext) -> callContext.getCompletableFuture()
                .completeExceptionally(
                    new JsonRpcConnectionClosedException("Connection for this client is closed.")
                )
            );
            callContexts.clear();
            LOGGER.info("The client is shutdown.");
        }
    }

    private void throwExceptionIfNotActive() throws JsonRpcConnectionClosedException {
        if (!isActive.get()) {
            throw new JsonRpcConnectionClosedException("Connection for this client is closed.");
        }
    }

    private void sendRequest(JsonNode requst) throws JsonRpcTransportException {
        LOGGER.debug("Sending request {}", requst);
        transporter.send(requst);
    }

    private static class CallContext<T> {

        private Class<T> returnType;

        private CompletableFuture<T> completableFuture = null;

        private ScheduledFuture timeoutFuture = null;

        CallContext(CompletableFuture<T> completableFuture, Class<T> returnType) {
            this.completableFuture = completableFuture;
            this.returnType = returnType;
        }

        CompletableFuture<T> getCompletableFuture() {
            return completableFuture;
        }

        Class<T> getReturnType() {
            return returnType;
        }

        ScheduledFuture getTimeoutFuture() {
            return timeoutFuture;
        }

        void setTimeoutFuture(ScheduledFuture timeoutFuture) {
            this.timeoutFuture = timeoutFuture;
        }
    }
}
