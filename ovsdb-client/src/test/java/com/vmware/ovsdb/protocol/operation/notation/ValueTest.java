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

import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class ValueTest {

  @Test
  public void testAtomValue() {
    assertEquals(Atom.string("123"), JsonUtil.deserializeNoException("\"123\"", Value.class));
    assertEquals(Atom.integer(24), JsonUtil.deserializeNoException("24", Value.class));
    assertEquals(Atom.bool(true), JsonUtil.deserializeNoException("true", Value.class));
    UUID uuid = UUID.randomUUID();
    assertEquals(
        Atom.uuid(uuid),
        JsonUtil.deserializeNoException("[\"uuid\",\"" + uuid + "\"]", Value.class)
    );
    assertEquals(
        Atom.namedUuid("uuid-name"),
        JsonUtil.deserializeNoException("[\"named-uuid\",\"uuid-name\"]", Value.class)
    );
  }

  @Test
  public void testSetValue() {
    assertEquals(
        Set.of("string1"),
        JsonUtil.deserializeNoException("[\"set\",[\"string1\"]]", Value.class)
    );
  }

  @Test
  public void testMapValue() {
    assertEquals(
        Map.of(ImmutableMap.of("key", "value")),
        JsonUtil.deserializeNoException("[\"map\",[[\"key\",\"value\"]]]", Value.class)
    );
  }

  @Test(expected = IOException.class)
  public void testInvalidValue() throws IOException {
    JsonUtil.deserialize("[\"value\",123]", Value.class);
  }

}
