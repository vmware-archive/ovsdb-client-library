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

package com.vmware.ovsdb.netty;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Client;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Server;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class JsonRpcHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());

  private final ExecutorService executorService;

  private final JsonRpcV1Client jsonRpcClient;

  private final JsonRpcV1Server jsonRpcServer;

  private CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);

  /**
   * Create a {@link JsonRpcHandler} that can handle JSON-RPC inbound messages.
   *
   * @param jsonRpcClient a {@link JsonRpcV1Client} used to handle JSON-RPC response
   * @param jsonRpcServer a {@link JsonRpcV1Server} used to handle JSON-RPC request
   * @param executorService an {@link ExecutorService} used to submit task
   */
  public JsonRpcHandler(
      JsonRpcV1Client jsonRpcClient, JsonRpcV1Server jsonRpcServer,
      ExecutorService executorService
  ) {
    this.jsonRpcClient = jsonRpcClient;
    this.jsonRpcServer = jsonRpcServer;
    this.executorService = executorService;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    JsonNode jsonNode = (JsonNode) msg;
    Runnable runnable = null;
    if (isRequestOrNotification(jsonNode)) {
      runnable = () -> {
        try {
          jsonRpcServer.handleRequest(jsonNode);
        } catch (JsonRpcException ex) {
          LOGGER.error("Failed to handle request " + jsonNode, ex);
        }
      };
    } else if (isResponse(jsonNode)) {
      runnable = () -> {
        try {
          jsonRpcClient.handleResponse(jsonNode);
        } catch (JsonRpcException ex) {
          LOGGER.error("Failed to handle response " + jsonNode, ex);
        }
      };
    } else {
      // Ignore non-JSON_RPC messages
      LOGGER.warn("Received invalid message {}", jsonNode);
    }

    if (runnable != null) {
      completableFuture = completableFuture.thenRunAsync(runnable, executorService);
    }
  }

  private boolean isRequestOrNotification(JsonNode jsonNode) {
    // Make sure "id", "method" and "params" fields exist
    // This dose NOT guarantee that the values are not null
    return jsonNode.get(JsonRpcConstant.ID) != null
        && jsonNode.get(JsonRpcConstant.METHOD) != null
        && jsonNode.get(JsonRpcConstant.PARAMS) != null;
  }

  private boolean isResponse(JsonNode jsonNode) {
    // Make sure "id", "result" and "error" fields exist
    // This dose NOT guarantee that the values are not null
    return jsonNode.get(JsonRpcConstant.ID) != null
        && jsonNode.get(JsonRpcConstant.RESULT) != null
        && jsonNode.get(JsonRpcConstant.ERROR) != null;
  }

}
