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
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class PairTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    String expectedString = "[\"string\",123]";
    Pair<String, Long> pair = new Pair<>(
        Atom.string("string"), Atom.integer(123));
    assertEquals(expectedString, JsonUtil.serialize(pair));
  }

  @Test
  public void testDeserialization() throws IOException {
    String jsonString = "[\"string\",123]";
    Pair<String, Long> pair = new Pair<>(
        Atom.string("string"), Atom.integer(123));
    assertEquals(pair, JsonUtil.deserialize(jsonString, Pair.class));
  }

  @Test(expected = IOException.class)
  public void testInvalidPair1() throws IOException {
    String jsonString = "[\"string\",123,true]";
    JsonUtil.deserialize(jsonString, Pair.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidPair2() throws IOException {
    String jsonString = "[\"string\"]";
    JsonUtil.deserialize(jsonString, Pair.class);
  }

  @Test
  public void testEquals() {
    UUID uuid = UUID.randomUUID();
    new EqualsTester()
        .addEqualityGroup(
            new Pair<>(Atom.string("uuid"), Atom.uuid(uuid)),
            new Pair<>(new Atom<>("uuid"), new Atom<>(Uuid.of(uuid)))
        ).testEquals();
  }
}
