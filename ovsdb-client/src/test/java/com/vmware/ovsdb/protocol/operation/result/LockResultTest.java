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

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.methods.LockResult;
import java.io.IOException;
import org.junit.Test;

public class LockResultTest {

  @Test
  public void testDeserialization() throws IOException {
    String json = "{\"locked\":true}";

    LockResult lockResult = JsonUtil.deserialize(json, LockResult.class);
    assertTrue(lockResult.isLocked());

    json = "{\"locked\":false}";

    lockResult = JsonUtil.deserialize(json, LockResult.class);
    assertFalse(lockResult.isLocked());
  }

  @Test(expected = IOException.class)
  public void testInvalidResult() throws IOException {
    String json = "{\"locked\":t}";
    JsonUtil.deserialize(json, LockResult.class);
  }
}
