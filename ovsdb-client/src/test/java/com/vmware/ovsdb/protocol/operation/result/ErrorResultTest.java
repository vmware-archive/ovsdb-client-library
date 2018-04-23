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

public class ErrorResultTest {

  private final String error = "constraint violation";

  private final String details = "duplicate name";

  @Test
  public void testDeserialization() {
    String jsonString = "{\"error\":\"" + error + "\", " + "\"details\":\"" + details + "\"}";

    ErrorResult expectedResult = new ErrorResult(error, details);

    assertEquals(
        expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
    );

    jsonString = "{\"error\":\"" + error + "\"}";

    expectedResult = new ErrorResult(error, null);

    assertEquals(
        expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
    );
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new ErrorResult(error, details), new ErrorResult(error, details))
        .testEquals();
  }
}
