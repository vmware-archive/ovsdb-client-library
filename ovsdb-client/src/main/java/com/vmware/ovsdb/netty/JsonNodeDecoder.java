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

import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

class JsonNodeDecoder extends JsonObjectDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());

  @Override
  protected void decode(
      ChannelHandlerContext ctx, ByteBuf in, List<Object> out
  ) throws Exception {
    List<Object> jsonByteBufs = new ArrayList<>();
    super.decode(ctx, in, jsonByteBufs);

    for (Object byteBuf : jsonByteBufs) {
      ByteBuf json = (ByteBuf) byteBuf;
      String textJson = json.toString(CharsetUtil.UTF_8);

      LOGGER.debug("Received message {} from channel {}", textJson, ctx.channel());
      out.add(JsonUtil.readTree(textJson));
      json.release();
    }
  }
}
