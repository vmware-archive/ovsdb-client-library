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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class RowTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    String expectedResult
        = "{\"name\":\"ls1\",\"description\":\"First Logical Switch\","
        + "\"tunnel_key\":5001}";

    Row row = new Row().column("name", Atom.string("ls1"))
        .column("description", Atom.string("First Logical Switch"))
        .column("tunnel_key", Atom.integer(5001));

    assertEquals(expectedResult, JsonUtil.serialize(row));

    row = new Row(ImmutableMap.of(
        "name", Atom.string("ls1"),
        "description", Atom.string("First Logical Switch"),
        "tunnel_key", Atom.integer(5001)
    ));

    assertEquals(expectedResult, JsonUtil.serialize(row));
  }

  @Test
  public void testDeserialization() throws IOException {
    Row expectedResult = new Row().column("name", Atom.string("ls1"))
        .column("description", Atom.string("First Logical Switch"))
        .column("tunnel_key", Atom.integer(5001));

    String textRow = JsonUtil.serialize(expectedResult.getColumns());

    assertEquals(
        expectedResult, JsonUtil.deserialize(textRow, Row.class));
  }

  @Test
  public void testDeserialization2() throws IOException {
    Row expectedResult = new Row()
        .stringColumn("name", "ls1")
        .mapColumn("vlan_stats", null);

    String textRow = JsonUtil.serialize(expectedResult.getColumns());

    Row row = JsonUtil.deserialize(textRow, Row.class);

    assertEquals("ls1", row.getStringColumn("name"));
    assertNull(row.getMapColumn("vlan_stats"));
  }
}
