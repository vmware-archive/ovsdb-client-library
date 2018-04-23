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

package com.vmware.ovsdb.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyManagerTest {

  @Test
  public void testBasic() {
    // A valid property should return the value in the property file
    assertEquals(10, PropertyManager.getIntProperty("rpc.timeout.sec", 42));
    // A nonexistent property should return default value
    assertEquals(42, PropertyManager.getLongProperty("non.existent.prop", 42));
    // An invalid integer property should return default value
    assertEquals(42, PropertyManager.getLongProperty("invalid.int", 42));
  }
}
