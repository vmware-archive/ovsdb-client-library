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
import com.vmware.ovsdb.service.OvsdbActiveConnectionConnector;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ScheduledExecutorService;

// TODO: Integration test for it
public class OvsdbActiveConnectionConnectorImpl implements OvsdbActiveConnectionConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());

  private final ScheduledExecutorService executorService;

  public OvsdbActiveConnectionConnectorImpl(ScheduledExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void connect(String ip, int port, ConnectionCallback connectionCallback) {
    connectTo(ip, port, null, connectionCallback);
  }

  @Override
  public void connectWithSsl(
      String ip, int port, SslContext sslContext, ConnectionCallback connectionCallback
  ) {
    connectTo(ip, port, sslContext, connectionCallback);
  }

  private void connectTo(
      String ip, int port, SslContext sslContext, ConnectionCallback connectionCallback
  ) {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new OvsdbChannelInitializer(
              sslContext, executorService, connectionCallback, false
          ));
      ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
      channelFuture.channel().closeFuture().addListener(future -> group.shutdownGracefully());
    } catch (InterruptedException ex) {
      LOGGER.error("Failed to connect to " + ip + ":" + port + " with ssl " + sslContext, ex);
      group.shutdownGracefully();
    }
  }
}
