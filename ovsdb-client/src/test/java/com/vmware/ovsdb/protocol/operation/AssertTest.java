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

package com.vmware.ovsdb.protocol.operation;

import static org.junit.Assert.assertEquals;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

public class AssertTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    Assert assrt = new Assert("myLock");
    String expectedResult = "{\"op\":\"assert\",\"lock\":\"myLock\"}";
    assertEquals(expectedResult, JsonUtil.serialize(assrt));
  }
}
