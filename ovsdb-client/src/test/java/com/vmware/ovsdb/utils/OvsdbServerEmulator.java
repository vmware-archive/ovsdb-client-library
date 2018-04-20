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

package com.vmware.ovsdb.utils;

import static com.google.common.base.Charsets.UTF_8;

import com.vmware.ovsdb.service.OvsdbConnectionInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import javax.net.ssl.SSLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// TODO: Can we  reuse code in listener and connector?
public abstract class OvsdbServerEmulator {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private Consumer<String> readCallback = null;

  private Channel channel = null;

  private OvsdbConnectionInfo connectionInfo = null;

  protected class OvsdbChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    private final CompletableFuture<Boolean> connectedFuture;

    private final boolean isServerChannel;

    OvsdbChannelInitializer(SslContext sslCtx, CompletableFuture<Boolean> connectedFuture,
        boolean isServerChannel) {
      this.sslCtx = sslCtx;
      this.connectedFuture = connectedFuture;
      this.isServerChannel = isServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) {
      LOGGER.info("New channel created: {}", ch);
      ChannelPipeline p = ch.pipeline();
      if (sslCtx != null) {
        SSLEngine engine = sslCtx.newEngine(ch.alloc());
        if (isServerChannel) {
          engine.setUseClientMode(false);
          engine.setNeedClientAuth(true);
        } else {
          engine.setUseClientMode(true);
        }
        p.addLast(new SslHandler(engine));
      }
      p.addLast(new LoggingHandler(LogLevel.INFO));
      p.addLast(new JsonObjectDecoder());
      p.addLast(new StringEncoder());
      p.addLast(new OvsdbInboundHandler(connectedFuture));
    }
  }

  protected class OvsdbInboundHandler extends ChannelInboundHandlerAdapter {

    private final CompletableFuture<Boolean> connectedFuture;

    OvsdbInboundHandler(CompletableFuture<Boolean> connectedFuture) {
      this.connectedFuture = connectedFuture;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
      LOGGER.info("Channel {} is now active", ctx.channel());
      channel = ctx.channel();
      SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
      if (sslHandler != null) {
        sslHandler.handshakeFuture().addListener(future -> {
          connectionInfo = OvsdbConnectionInfo.fromChannel(channel);
          connectedFuture.complete(true);
        });
      } else {
        connectionInfo = OvsdbConnectionInfo.fromChannel(channel);
        connectedFuture.complete(true);
      }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      connectionInfo = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      ByteBuf byteBuf = (ByteBuf) msg;
      String data = byteBuf.toString(UTF_8);
      LOGGER.info("Read data {} from channel", data, ctx.channel());
      if (readCallback != null) {
        LOGGER.debug("Calling read callback {}", readCallback);
        readCallback.accept(data);
      } else {
        ctx.fireChannelRead(msg);
      }
    }
  }

  /**
   * Register a callback that is notified when this client reads a string from the channel.
   *
   * @param readCallback the callback
   */
  public void registerReadCallback(Consumer<String> readCallback) {
    this.readCallback = readCallback;
    LOGGER.debug("Read callback {} registered", readCallback);
  }

  /**
   * Write to the connection.
   *
   * @param data data to write to the connection
   */
  public void write(String data) {
    channel.writeAndFlush(data);
  }

  public OvsdbConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

  /**
   * Disconnect from the peer.
   */
  public CompletableFuture<Boolean> disconnect() {
    CompletableFuture<Boolean> disconnectFuture = new CompletableFuture<>();
    if (channel != null) {
      LOGGER.info("Closing channel {}", channel);
      channel.closeFuture().addListener(future -> {
        channel = null;
        disconnectFuture.complete(future.isSuccess());
      });
      channel.close();
    } else {
      disconnectFuture.complete(true);
    }
    return disconnectFuture;
  }
}
