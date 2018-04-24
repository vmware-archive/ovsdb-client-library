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
import org.junit.Test;

import java.io.IOException;

public class EmptyResultTest {

  @Test
  public void testDeserialization() {
    String jsonString = "{}";

    EmptyResult expectedResult = new EmptyResult();

    assertEquals(
        expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
    );
  }

  @Test(expected = IOException.class)
  public void testInvalidResult1() throws IOException {
    JsonUtil.deserialize("{\"count\":1, \"error\":\"some error\"}", OperationResult.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidResult2() throws IOException {
    JsonUtil.deserialize("{\"invalid\":\"something\"}", OperationResult.class);
  }

  @Test
  public void testEquals() {
    new EqualsTester().addEqualityGroup(new EmptyResult(), new EmptyResult()).testEquals();
  }
}
