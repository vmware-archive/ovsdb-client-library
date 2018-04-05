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

package com.vmware.ovsdb.protocol.operation.notation;

import static org.junit.Assert.assertEquals;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;

public class SetTest {

  private static final String jsonString
      = "[\"set\",[\"string1\",\"string2\"]]";

  private static final Set set = Set.of("string1", "string2");

  @Test
  public void testSerialization() throws JsonProcessingException {
    assertEquals(jsonString, JsonUtil.serialize(set));
  }

  @Test
  public void testDeserialization() throws IOException {
    assertEquals(set, JsonUtil.deserialize(jsonString, Set.class));
  }

  @Test
  public void testAtomSetDeserialization() throws IOException {
    java.util.Set<String> set1 = new HashSet<>();
    set1.add("1234");
    set1.add("5678");
    set1.add("91011");
    java.util.Set<String> set2 = new HashSet<>();
    set2.add("1234");
    set2.add("5678");
    set2.add("91011");

    java.util.Map<java.util.Set<String>, String> map = new HashMap<>();
    map.put(set1, "123");

    assertEquals(
        Set.of("string1"),
        JsonUtil.deserialize("\"string1\"", Set.class)
    );
  }

  @Test(expected = IOException.class)
  public void testInvalidSet1() throws IOException {

    JsonUtil.deserialize("[[123, 456]]", Set.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidSet2() throws IOException {
    JsonUtil.deserialize("[\"not-set\",[123, 456]]", Set.class);
  }
}
