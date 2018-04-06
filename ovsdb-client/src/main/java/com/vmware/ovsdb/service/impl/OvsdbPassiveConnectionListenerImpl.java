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

package com.vmware.ovsdb.service.impl;

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.netty.OvsdbChannelInitializer;
import com.vmware.ovsdb.service.OvsdbPassiveConnectionListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

public class OvsdbPassiveConnectionListenerImpl
    implements OvsdbPassiveConnectionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());

  // Map from port to server Channel
  private final ConcurrentMap<Integer, Channel> serverChannelMap = new ConcurrentHashMap<>();

  // Map from port to server status (started/not started)
  private final ConcurrentMap<Integer, Boolean> serverStatusMap = new ConcurrentHashMap<>();

  private final ScheduledExecutorService executorService;

  public OvsdbPassiveConnectionListenerImpl(ScheduledExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void startListening(
      int port, ConnectionCallback connectionCallback
  ) {
    isListeningCheckWithThrow(port);
    executorService.submit(() -> startListeningOnPort(port, null, connectionCallback));
  }

  @Override
  public void startListeningWithSsl(
      int port, SslContext sslContext, ConnectionCallback connectionCallback
  ) {
    isListeningCheckWithThrow(port);
    executorService.submit(() -> startListeningOnPort(port, sslContext, connectionCallback));
  }

  @Override
  public void stopListening(int port) {
    Channel serverChannel = serverChannelMap.remove(port);
    if (serverChannel != null) {
      LOGGER.info("Closing server channel: {}", serverChannel);
      serverChannel.close();
      serverStatusMap.remove(port);
    }
  }

  private void startListeningOnPort(
      int port, final SslContext sslContext, ConnectionCallback connectionCallback
  ) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 100)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new OvsdbChannelInitializer(
              sslContext, executorService, connectionCallback, true
          ));

      serverBootstrap.option(
          ChannelOption.RCVBUF_ALLOCATOR,
          new AdaptiveRecvByteBufAllocator(65535, 65535, 65535)
      );

      // Start the server.
      ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
      serverChannelMap.put(port,  channelFuture.channel());

      // Wait until the server socket is closed.
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException ex) {
      LOGGER.error("Netty server is interrupted while starting listener at port " + port, ex);
    } finally {
      // Shut down all event loops to terminate all threads.
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      LOGGER.info("Ovsdb listener at port {} stopped.", port);
    }
  }

  private void isListeningCheckWithThrow(int port) {
    if (serverStatusMap.putIfAbsent(port, true) != null) {
      throw new IllegalStateException("A listener has already started at port " + port);
    }
  }
}
