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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;

/**
 * An ovsdb-server emulator that passively listening on a port.
 * This emulator assumes there is only one connection will be established.
 */
public class PassiveOvsdbServerEmulator extends OvsdbServerEmulator {

  private Channel serverChannel;

  private final int port;

  /**
   * Create a {@link PassiveOvsdbServerEmulator} object.
   *
   * @param port the port that this ovsdb server will listen to
   */
  public PassiveOvsdbServerEmulator(int port) {
    this.port = port;
  }

  /**
   * Start listening on the port.
   */
  public CompletableFuture<Boolean> startListening() {
    return startListening(null);
  }

  /**
   * Start listening on the port with SSL enabled.
   *
   * @param sslCtx the {@link SslContext} for the connection
   */
  public CompletableFuture<Boolean> startListeningWithSsl(SslContext sslCtx) {
    return startListening(sslCtx);
  }

  /**
   * Stop listening on the port.
   */
  public CompletableFuture<Boolean> stopListening() {
    CompletableFuture<Boolean> stopFuture = new CompletableFuture<>();
    if (serverChannel != null) {
      serverChannel.closeFuture().addListener(future -> stopFuture.complete(future.isSuccess()));
      serverChannel.close();
    } else {
      stopFuture.complete(true);
    }
    return stopFuture;
  }

  private CompletableFuture<Boolean> startListening(SslContext sslCtx) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .childHandler(new OvsdbChannelInitializer(sslCtx, new CompletableFuture<>(), true))
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);
    ChannelFuture channelFuture = b.bind(port);
    CompletableFuture<Boolean> listenFuture = new CompletableFuture<>();
    channelFuture.addListener(future -> listenFuture.complete(future.isSuccess()));
    serverChannel = channelFuture.channel();
    serverChannel.closeFuture().addListener(future -> {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    });
    return listenFuture;
  }
}
