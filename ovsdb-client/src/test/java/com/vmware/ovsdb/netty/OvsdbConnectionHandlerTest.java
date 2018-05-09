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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;

public class OvsdbConnectionHandlerTest {

  private final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

  private final IdleStateEvent idleStateEvent = mock(IdleStateEvent.class);

  private static final int MAX_READ_IDLE_BEFORE_CLOSE = PropertyManager
      .getIntProperty("channel.read.idle.max", 3);

  @Before
  public void setUp() {
    reset(ctx);
  }

  @Test
  public void testReadRegularMessage() {
    OvsdbConnectionHandler heartBeatHandler = newOvsdbConnectionHandler();
    JsonNode jsonNode = JsonNodeFactory.instance.objectNode();
    ((ObjectNode) jsonNode).put("id", "123");
    ((ObjectNode) jsonNode).put("method", "echo");
    ((ObjectNode) jsonNode).putArray("params");
    heartBeatHandler.channelRead(ctx, jsonNode);

    verify(ctx).fireChannelRead(jsonNode);
  }

  @Test
  public void testReadRegularEvent() {
    OvsdbConnectionHandler heartBeatHandler = newOvsdbConnectionHandler();
    Object userEvent = new Object();
    heartBeatHandler.userEventTriggered(ctx, userEvent);

    verify(ctx).fireUserEventTriggered(userEvent);
  }

  @Test
  public void testChannelReadTimeout() {
    OvsdbConnectionHandler heartBeatHandler = newOvsdbConnectionHandler();
    for (int i = 0; i < MAX_READ_IDLE_BEFORE_CLOSE; i++) {
      heartBeatHandler.userEventTriggered(ctx, idleStateEvent);
    }
    verify(ctx).close();
  }

  @Test
  public void testReceiveHeartbeatResponseInTime() {
    OvsdbConnectionHandler heartBeatHandler = newOvsdbConnectionHandler();
    for (int i = 0; i < MAX_READ_IDLE_BEFORE_CLOSE - 1; i++) {
      heartBeatHandler.userEventTriggered(ctx, idleStateEvent);
    }
    JsonNode jsonNode = JsonNodeFactory.instance.objectNode();
    ((ObjectNode) jsonNode).put("id", "heartbeat-1");
    ((ObjectNode) jsonNode).put("method", "echo");
    ((ObjectNode) jsonNode).putArray("params");

    heartBeatHandler.channelRead(ctx, jsonNode);
    heartBeatHandler.userEventTriggered(ctx, idleStateEvent);
    verify(ctx, times(0)).close();
  }

  private OvsdbConnectionHandler newOvsdbConnectionHandler() {
    return new OvsdbConnectionHandler(mock(ConnectionCallback.class),
        mock(ScheduledExecutorService.class));
  }
}
