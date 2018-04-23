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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.junit.Test;

public class SetTest {

  private static final String jsonString = "[\"set\",[\"string2\",\"string1\"]]";

  private static final Set set = Set.of("string1", "string2");

  @Test
  public void testSerialization() throws JsonProcessingException {
    assertEquals(jsonString, JsonUtil.serialize(set));
  }

  @Test
  public void testDeserialization() throws IOException {
    assertEquals(set, JsonUtil.deserialize(jsonString, Set.class));
  }

  /**
   * Test the case when an {@literal <atom>} is a {@literal <set>}.
   */
  @Test
  public void testAtomSetDeserialization() throws IOException {
    assertEquals(Set.of("string1"), JsonUtil.deserialize("\"string1\"", Set.class));
    assertEquals(Set.of(42L), JsonUtil.deserialize("42", Set.class));
    assertEquals(Set.of(true), JsonUtil.deserialize("true", Set.class));
    UUID uuid = UUID.randomUUID();
    assertEquals(Set.of(new Uuid(uuid)), JsonUtil.deserialize("[\"uuid\", \"" + uuid + "\"]", Set.class));
    assertEquals(Set.of(4.2), JsonUtil.deserialize("4.2", Set.class));
  }

  @Test(expected = IOException.class)
  public void testInvalidSet1() throws IOException {
    JsonUtil.deserialize("[[123, 456]]", Set.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidSet2() throws IOException {
    JsonUtil.deserialize("[\"not-set\",[123, 456]]", Set.class);
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new Set(), new Set(Collections.emptySet()))
        .addEqualityGroup(set, Set.of(ImmutableSet.of("string1", "string2")))
        .testEquals();
    java.util.Set<String> nullSet = null;
    assertNull(Set.of(nullSet));
  }
}
