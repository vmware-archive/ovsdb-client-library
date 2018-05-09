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

public class JsonRpcV1RequestTest {

  private final JsonRpcV1Request request =
      new JsonRpcV1Request("id-1", "foo", "string", 123, 4.5, true);

  private final String jsonString = "{\"" + JsonRpcConstant.METHOD + "\":\"foo\","
      + "\"" + JsonRpcConstant.PARAMS + "\":[\"string\",123,4.5,true],\""
      + JsonRpcConstant.ID + "\":\"id-1\"}";

  @Test
  public void testSerialize() {
    assertEquals(jsonString, JsonUtil.serializeNoException(request));
  }

  @Test
  public void testDeserialize() {
    assertEquals(request, JsonUtil.deserializeNoException(jsonString, JsonRpcV1Request.class));
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(
            new JsonRpcV1Request("id", "method", 123, 4.56),
            new JsonRpcV1Request("id", "method", 123, 4.56)
        )
        .addEqualityGroup(
            new JsonRpcV1Request(null, "method"),
            new JsonRpcV1Request(null, "method")
        ).testEquals();
  }
}
