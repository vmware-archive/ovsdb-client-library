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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.ECHO;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HeartBeatHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String KEY_CHANNEL_READ_IDLE_MAX = "channel.read.idle.max";

    private static final int DEFAULT_READ_IDLE_MAX = 3;

    // At most 3 heartbeat should be sent before closing the channel
    private static final int MAX_READ_IDLE_BEFORE_CLOSE = PropertyManager
        .getIntProperty(KEY_CHANNEL_READ_IDLE_MAX, DEFAULT_READ_IDLE_MAX);

    private static final String HEARTBEAT_PREFIX = "heartbeat-";

    private int heartbeatCount = 0;

    private int readIdleCount = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
        throws Exception {
        readIdleCount = 0;
        // Don't send heartbeat response to user
        if (!isHeartbeatResponse((JsonNode) msg)) {
            ctx.fireChannelRead(msg);
        } else {
            LOGGER.debug("Received heartbeat response {}", msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
        throws Exception {
        if (evt instanceof IdleStateEvent) {
            ++readIdleCount;
            LOGGER.warn(
                "Read idle of {} for {} times", ctx.channel(), readIdleCount);
            if (readIdleCount >= MAX_READ_IDLE_BEFORE_CLOSE) {
                LOGGER.warn("Read idle time reaches the threshold. Closing the channel to {}",
                    ctx.channel());
                ctx.close();
            } else {
                String heartbeatMsg = getHeartbeatMsg();
                LOGGER.info("Sending heartbeat {} to channel {}", heartbeatMsg, ctx.channel());
                ctx.writeAndFlush(heartbeatMsg);
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    private boolean isHeartbeatResponse(JsonNode msgNode) {
        JsonNode idNode = msgNode.get(JsonRpcConstant.ID);
        return idNode != null && idNode.asText().startsWith(HEARTBEAT_PREFIX);
    }

    private String getHeartbeatMsg() {
        String id = HEARTBEAT_PREFIX + heartbeatCount++;
        JsonRpcV1Request jsonRpcRequest = new JsonRpcV1Request(id, ECHO);
        return JsonUtil.serializeNoException(jsonRpcRequest);
    }
}
