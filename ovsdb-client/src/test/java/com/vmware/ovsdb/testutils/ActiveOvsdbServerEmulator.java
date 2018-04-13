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

package com.vmware.ovsdb.testutils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;

/**
 * An ovsdb-server emulator that actively connects a controller.
 */
public class ActiveOvsdbServerEmulator extends OvsdbServerEmulator {

  private final String host;

  private final int port;

  /**
   * Create an {@link ActiveOvsdbServerEmulator} object.
   *
   * @param host ip address of the controller that this ovsdb-server emulator will connect to
   * @param port port of the controller that this ovsdb-server emulator will connect to
   */
  public ActiveOvsdbServerEmulator(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Connect to a server on host:port. This is a asynchronous call.
   */
  public CompletableFuture<Boolean> connect() {
    return doConnect(null);
  }

  /**
   * Connect to a server on host:port. This is a synchronous call.
   *
   * @param sslCtx the {@link SslContext} for the connection
   */
  public CompletableFuture<Boolean> connectWithSsl(SslContext sslCtx) {
    return doConnect(sslCtx);
  }

  private CompletableFuture<Boolean> doConnect(SslContext sslCtx) {
    CompletableFuture<Boolean> connectedFuture = new CompletableFuture<>();
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    b.group(group).channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .handler(new OvsdbChannelInitializer(sslCtx, connectedFuture, false));
    b.connect(host, port).channel().closeFuture()
        .addListener(future -> group.shutdownGracefully());
    return connectedFuture;
  }
}
