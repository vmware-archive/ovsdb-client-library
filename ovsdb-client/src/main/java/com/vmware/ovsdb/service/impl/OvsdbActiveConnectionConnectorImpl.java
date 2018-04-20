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

import static com.vmware.ovsdb.netty.OvsdbChannelInitializer.newOvsdbChannelInitializer;

import com.vmware.ovsdb.service.OvsdbActiveConnectionConnector;
import com.vmware.ovsdb.service.OvsdbClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class OvsdbActiveConnectionConnectorImpl implements OvsdbActiveConnectionConnector {

  private final ScheduledExecutorService executorService;

  public OvsdbActiveConnectionConnectorImpl(ScheduledExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public CompletableFuture<OvsdbClient> connect(String ip, int port) {
    return doConnect(ip, port, null);
  }

  @Override
  public CompletableFuture<OvsdbClient> connectWithSsl(String ip, int port, SslContext sslContext) {
    return doConnect(ip, port, sslContext);
  }

  private CompletableFuture<OvsdbClient> doConnect(String ip, int port, SslContext sslContext) {
    CompletableFuture<OvsdbClient> ovsdbClientFuture = new CompletableFuture<>();
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .handler(newOvsdbChannelInitializer(sslContext, executorService, ovsdbClientFuture));
    ChannelFuture channelFuture = bootstrap.connect(ip, port);
    channelFuture.channel().closeFuture()
        .addListener(future -> group.shutdownGracefully());
    return ovsdbClientFuture;
  }
}
