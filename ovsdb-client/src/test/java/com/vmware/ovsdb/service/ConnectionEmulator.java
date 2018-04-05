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

package com.vmware.ovsdb.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emulate an OVSDB connection. It can initiate the connection to the OVSDB manager and write to the
 * channel.
 */
class ConnectionEmulator {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SslContext sslCtx;

  private final Object connected = new Object();

  private ChannelFuture channelFuture = null;

  private SocketChannel socketChannel = null;

  private Consumer<String> readCallback = null;

  ConnectionEmulator(SslContext sslCtx) {
    this.sslCtx = sslCtx;
  }

  ConnectionEmulator() {
    this(null);
  }

  void registerReadCallback(Consumer<String> readCallback) {
    this.readCallback = readCallback;
  }

  void connect(String host, int port) throws InterruptedException {
    new Thread(() -> connectTo(host, port)).start();
    synchronized (connected) {
      // Wait until the channel is active
      connected.wait();
    }
  }

  void write(String data) {
    socketChannel.writeAndFlush(data);
  }

  void disconnect() {
    if (channelFuture != null) {
      channelFuture.channel().close();
      channelFuture = null;
    }
  }

  private void connectTo(String host, int port) {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              if (sslCtx != null) {
                p.addLast(
                    sslCtx.newHandler(ch.alloc(), host, port));
              }
              p.addLast(new LoggingHandler(LogLevel.INFO));
              p.addLast(new StringDecoder());
              p.addLast(new StringEncoder());
              p.addLast(new ChannelInboundHandlerAdapter() {

                @Override
                public void channelActive(final ChannelHandlerContext ctx) {
                  LOGGER.info("Channel {} is now active", ctx.channel());
                  synchronized (connected) {
                    // Unblock the connect call
                    connected.notify();
                  }
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg)
                    throws Exception {
                  LOGGER.info("Read data {} from channel", msg, ctx.channel());
                  if (readCallback != null) {
                    readCallback.accept((String) msg);
                  } else {
                    ctx.fireChannelRead(msg);
                  }
                }
              });
              socketChannel = ch;
            }
          });

      channelFuture = b.connect(host, port).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      // Shut down the event loop to terminate all threads.
      group.shutdownGracefully();
    }
  }
}
