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

package com.vmware.ovsdb.protocol.operation.result;

import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import org.junit.Test;

import java.util.UUID;

public class InsertResultTest {

  @Test
  public void testDeserialization() {
    String jsonString
        = "{\"uuid\":[\"uuid\","
        + "\"00000000-0000-0000-0000-000000000000\"]}";

    Uuid uuid = new Uuid(
        UUID.fromString("00000000-0000-0000-0000-000000000000"));
    InsertResult expectedResult = new InsertResult(uuid);

    assertEquals(
        expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
    );
  }

  @Test
  public void testEquals() {
    UUID uuid = UUID.randomUUID();
    new EqualsTester()
        .addEqualityGroup(new InsertResult(Uuid.of(uuid)), new InsertResult(new Uuid(uuid)))
        .testEquals();
  }
}
