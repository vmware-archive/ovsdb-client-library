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

package com.vmware.ovsdb.jsonrpc.v1.model;

import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

public class JsonRpcV1ResponseTest {

  private final JsonRpcV1Response response =
      new JsonRpcV1Response("value", null, "id");

  private final String jsonString = "{\"" + JsonRpcConstant.RESULT + "\":\"value\",\""
      + JsonRpcConstant.ERROR + "\":null,\"" + JsonRpcConstant.ID + "\":\"id\"}";

  @Test
  public void testSerialize() {
    assertEquals(jsonString, JsonUtil.serializeNoException(response));
  }

  @Test
  public void testDeserialize() {
    assertEquals(response, JsonUtil.deserializeNoException(jsonString, JsonRpcV1Response.class));
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(
            new JsonRpcV1Response("value", null, "id"),
            response
        )
        .addEqualityGroup(
            new JsonRpcV1Response(null, "error", "id"),
            new JsonRpcV1Response(null, "error", "id")
        ).testEquals();
  }
}
