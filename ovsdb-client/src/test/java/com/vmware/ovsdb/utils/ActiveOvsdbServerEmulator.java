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
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A netty client that can connects to any server.
 */
public class ActiveOvsdbServerEmulator {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private SocketChannel channel = null;

  private Consumer<String> readCallback = null;

  private OvsdbConnectionInfo connectionInfo = null;

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
   * Connect to a server on host:port. This is a synchronous call.
   */
  public void connect(String host, int port) throws InterruptedException {
    connectTo(host, port, null);
  }

  /**
   * Connect to a server on host:port. This is a synchronous call.
   *
   * @param host the host to connect
   * @param port the port on the host to connect
   * @param sslCtx the {@link SslContext} for the connection
   */
  public void connectWithSsl(String host, int port, SslContext sslCtx) throws InterruptedException {
    connectTo(host, port, sslCtx);
  }

  /**
   * Write the the connection.
   *
   * @param data data to write to the connection
   */
  public void write(String data) {
    channel.writeAndFlush(data);
  }

  /**
   * Disconnect from the server.
   */
  public void disconnect() {
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  private void connectTo(String host, int port, SslContext sslCtx) throws InterruptedException {
    CompletableFuture<Boolean> connectedFuture = new CompletableFuture<>();
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              if (sslCtx != null) {
                p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
              }
              p.addLast(new LoggingHandler(LogLevel.INFO));
              p.addLast(new JsonObjectDecoder());
              p.addLast(new StringEncoder());
              p.addLast(new OvsdbInboundHandler(connectedFuture));
              channel = ch;
            }
          });

      b.connect(host, port).sync().channel().closeFuture()
          .addListener(future -> group.shutdownGracefully());
      connectedFuture.join();
    } catch (InterruptedException e) {
      LOGGER.error("Error: ", e);
      group.shutdownGracefully();
    }
  }

  private class OvsdbInboundHandler extends ChannelInboundHandlerAdapter {

    private CompletableFuture<Boolean> connectedFuture;

    private OvsdbInboundHandler(CompletableFuture<Boolean> connectedFuture) {
      this.connectedFuture = connectedFuture;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
      LOGGER.info("Channel {} is now active", ctx.channel());

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

  public OvsdbConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }
}
