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

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;

public class OvsdbChannelInitializer extends ChannelInitializer<SocketChannel> {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());

  private static final String KEY_CHANNEL_READ_IDLE_TIMEOUT_SEC = "channel.read.idle.timeout.sec";

  private static final long DEFAULT_READ_IDLE_TIMEOUT_SEC = 30;

  private static long READ_IDLE_TIMEOUT = PropertyManager
      .getLongProperty(KEY_CHANNEL_READ_IDLE_TIMEOUT_SEC, DEFAULT_READ_IDLE_TIMEOUT_SEC);

  private final SslContext sslContext;

  private final ConnectionCallback connectionCallback;

  private final ScheduledExecutorService executorService;

  /**
   * Create a {@link OvsdbChannelInitializer} object.
   *
   * @param sslContext the SSL context
   * @param executorService an {@link ScheduledExecutorService} object
   * @param connectionCallback will be called then a new connection is established
   */
  public OvsdbChannelInitializer(
      SslContext sslContext,
      ScheduledExecutorService executorService,
      ConnectionCallback connectionCallback
  ) {
    this.sslContext = sslContext;
    this.executorService = executorService;
    this.connectionCallback = connectionCallback;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    LOGGER.info("New passive channel created: {}", ch);

    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(
        "idleStateHandler",
        new IdleStateHandler(READ_IDLE_TIMEOUT, 0, 0, TimeUnit.SECONDS)
    );
    if (sslContext != null) {
      SSLEngine engine = sslContext.newEngine(ch.alloc());
      engine.setUseClientMode(false);
      engine.setNeedClientAuth(true);
      pipeline.addLast("ssl", new SslHandler(engine));
    }
    pipeline.addLast("decoder", new JsonNodeDecoder());
    pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
    pipeline.addLast("logger", new LoggingHandler(LogLevel.TRACE));
    pipeline.addLast("heartbeatHandler", new HeartBeatHandler());
    pipeline.addLast("ovsdbClientHandler",
        new OvsdbClientHandler(connectionCallback, executorService));
    pipeline.addLast("exceptionHandler", new ExceptionHandler());
  }

}
