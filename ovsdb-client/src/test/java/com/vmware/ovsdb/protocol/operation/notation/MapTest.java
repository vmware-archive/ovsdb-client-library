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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class MapTest {

  private static final String jsonString
      = "[\"map\",[[\"key1\",\"value1\"],[\"key2\",\"value2\"]]]";

  private static final Map<String, String> map = new Map<>(
      ImmutableList.of(
          new Pair<>(Atom.string("key1"), Atom.string("value1")),
          new Pair<>(Atom.string("key2"), Atom.string("value2"))
      )
  );

  @Test
  public void testSerialization() throws JsonProcessingException {
    assertEquals(jsonString, JsonUtil.serialize(map));
  }

  @Test
  public void testDeserialization() throws IOException {
    assertEquals(map, JsonUtil.deserialize(jsonString, Map.class));
  }

  @Test(expected = IOException.class)
  public void testInvalidMap1() throws IOException {
    JsonUtil.deserialize("[[123, 456]]", Map.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidMap2() throws IOException {
    JsonUtil.deserialize("[\"not-map\",[123, 456]]", Map.class);
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(map, Map.of(ImmutableMap.of("key1", "value1", "key2", "value2")))
        .testEquals();
  }
}
