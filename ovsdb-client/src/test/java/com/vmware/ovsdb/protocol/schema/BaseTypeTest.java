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

package com.vmware.ovsdb.protocol.schema;

import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;

public class BaseTypeTest {

  @Test(expected = IOException.class)
  public void testInvalidType() throws IOException {
    JsonUtil.deserialize("{\"type\": \"double\"}", BaseType.class);
  }

  @Test(expected = IOException.class)
  public void testMissingType() throws IOException {
    JsonUtil.deserialize("{\"minInteger\": 0, \"maxInteger\":1}", BaseType.class);
  }

}
