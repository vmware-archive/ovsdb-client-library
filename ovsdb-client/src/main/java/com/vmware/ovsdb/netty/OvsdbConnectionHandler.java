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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.ECHO;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.service.OvsdbClient;
import com.vmware.ovsdb.service.impl.OvsdbClientImpl;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

class OvsdbConnectionHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());

  // At most 3 heartbeat should be sent before closing the channel
  private static final int MAX_READ_IDLE_BEFORE_CLOSE = PropertyManager
      .getIntProperty("channel.read.idle.max", 3);

  private static final String HEARTBEAT_PREFIX = "heartbeat-";

  private int heartbeatCount = 0;

  private int readIdleCount = 0;

  private final ConnectionCallback connectionCallback;

  private final CompletableFuture<OvsdbClient> ovsdbClientFuture;

  private final ScheduledExecutorService executorService;

  private OvsdbClient ovsdbClient;

  /**
   * Create an {@link OvsdbConnectionHandler} with a connection callback.
   * This should be called in passive mode.
   *
   * @param connectionCallback will be called when there is a new connection
   * @param executorService a {@link ScheduledExecutorService} used for asynchronous operations
   */
  OvsdbConnectionHandler(
      ConnectionCallback connectionCallback, ScheduledExecutorService executorService
  ) {
    this(null, connectionCallback, executorService);
  }

  /**
   * Create an {@link OvsdbConnectionHandler} with a {@link CompletableFuture}.
   * This should be called in active mode.
   *
   * @param ovsdbClientFuture will complete with an {@link OvsdbClient} after the connection is done
   * @param executorService a {@link ScheduledExecutorService} used for asynchronous operations
   */
  OvsdbConnectionHandler(
      CompletableFuture<OvsdbClient> ovsdbClientFuture, ScheduledExecutorService executorService
  ) {
    this(ovsdbClientFuture, null, executorService);
  }

  private OvsdbConnectionHandler(
      CompletableFuture<OvsdbClient> ovsdbClientFuture, ConnectionCallback connectionCallback,
      ScheduledExecutorService executorService
  ) {
    this.ovsdbClientFuture = ovsdbClientFuture;
    this.connectionCallback = connectionCallback;
    this.executorService = executorService;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) {
    Channel channel = ctx.channel();
    LOGGER.info("Channel {} is now active", channel);

    SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
    // SSL is enabled, notify connection callback only after the handshake is done
    if (sslHandler != null) {
      sslHandler.handshakeFuture().addListener(future -> notifyConnection(channel));
    } else {
      notifyConnection(channel);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    readIdleCount = 0;
    // Don't send heartbeat response to user
    if (!isHeartbeatResponse((JsonNode) msg)) {
      ctx.fireChannelRead(msg);
    } else {
      LOGGER.debug("Received heartbeat response {}", msg);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    LOGGER.info("Channel {} is now inactive", ctx.channel());
    if (connectionCallback != null && ovsdbClient != null) {
      executorService.submit(() -> connectionCallback.disconnected(ovsdbClient));
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
    if (evt instanceof IdleStateEvent) {
      ++readIdleCount;
      LOGGER.warn(
          "Read idle of {} for {} times", ctx.channel(), readIdleCount);
      if (readIdleCount >= MAX_READ_IDLE_BEFORE_CLOSE) {
        LOGGER.warn("Read idle time reaches the threshold. Closing the channel to {}",
            ctx.channel());
        ctx.close();
      } else {
        String heartbeatMsg = getHeartbeatMsg();
        LOGGER.debug("Sending heartbeat {} to channel {}", heartbeatMsg, ctx.channel());
        ctx.writeAndFlush(heartbeatMsg);
      }
    } else {
      ctx.fireUserEventTriggered(evt);
    }
  }

  private boolean isHeartbeatResponse(JsonNode msgNode) {
    JsonNode idNode = msgNode.get(JsonRpcConstant.ID);
    return idNode != null && idNode.asText().startsWith(HEARTBEAT_PREFIX);
  }

  private String getHeartbeatMsg() {
    String id = HEARTBEAT_PREFIX + heartbeatCount++;
    JsonRpcV1Request jsonRpcRequest = new JsonRpcV1Request(id, ECHO);
    return JsonUtil.serializeNoException(jsonRpcRequest);
  }

  private void notifyConnection(Channel channel) {
    ovsdbClient = new OvsdbClientImpl(executorService, channel);
    if (connectionCallback != null) {
      executorService.submit(() -> connectionCallback.connected(ovsdbClient));
    } else {
      ovsdbClientFuture.complete(ovsdbClient);
    }
  }
}
