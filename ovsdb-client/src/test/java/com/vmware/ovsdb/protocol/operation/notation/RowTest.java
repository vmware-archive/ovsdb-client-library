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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class RowTest {

  private final UUID uuid = UUID.randomUUID();

  private final Row row = new Row()
      .stringColumn("string_column", "A string")
      .integerColumn("integer_column", 42L)
      .boolColumn("bool_column", true)
      .uuidColumn("uuid_column", Uuid.of(uuid))
      .namedUuidColumn("named-uuid", "uuid-name")
      .mapColumn("map_column", ImmutableMap.of("key", "value"))
      .setColumn("set_column", ImmutableSet.of("value1", "value2"));

  private final String jsonString = "{\"string_column\":\"A string\","
      + "\"integer_column\":42,\"bool_column\":true,\"uuid_column\":[\"uuid\",\"" + uuid + "\"],"
      + "\"named-uuid\":[\"named-uuid\",\"uuid-name\"],"
      + "\"map_column\":[\"map\",[[\"key\",\"value\"]]],"
      + "\"set_column\":[\"set\",[\"value1\",\"value2\"]]}";

  @Test
  public void testSerialization() throws JsonProcessingException {
    // Note: the serialized row may not preserve the order
    String serializedRow = JsonUtil.serialize(row);
    assertTrue(serializedRow.contains("\"string_column\":\"A string\""));
    assertTrue(serializedRow.contains("\"integer_column\":42"));
    assertTrue(serializedRow.contains("\"bool_column\":true"));
    assertTrue(serializedRow.contains("\"uuid_column\":[\"uuid\",\"" + uuid + "\"]"));
    assertTrue(serializedRow.contains("\"named-uuid\":[\"named-uuid\",\"uuid-name\"]"));
    assertTrue(serializedRow.contains("\"map_column\":[\"map\",[[\"key\",\"value\"]]]"));
    assertTrue(serializedRow.contains("\"set_column\":[\"set\",[\"value2\",\"value1\"]]"));
  }

  @Test
  public void testDeserialization() throws IOException {
    Row deserialized = JsonUtil.deserialize(jsonString, Row.class);
    assertEquals(row, deserialized);

    assertEquals("A string", deserialized.getStringColumn("string_column"));
    assertEquals(new Long(42), deserialized.getIntegerColumn("integer_column"));
    assertEquals(Boolean.TRUE, deserialized.getBooleanColumn("bool_column"));
    assertEquals(new Uuid(uuid), deserialized.getUuidColumn("uuid_column"));
    assertEquals(ImmutableMap.of("key", "value"), deserialized.getMapColumn("map_column"));
    assertEquals(ImmutableSet.of("value1", "value2"), deserialized.getSetColumn("set_column"));
  }

  @Test
  public void testDeserialization2() throws IOException {
    Row expectedResult = new Row()
        .stringColumn("name", "ls1")
        .mapColumn("vlan_stats", null)
        .setColumn("set_column", null);

    String textRow = JsonUtil.serialize(expectedResult.getColumns());

    Row row = JsonUtil.deserialize(textRow, Row.class);

    assertEquals("ls1", row.getStringColumn("name"));
    assertNull(row.getMapColumn("vlan_stats"));
    assertNull(row.getSetColumn("set_column"));
  }

  @Test
  public void testEquals() {
    java.util.Map<String, Value> columns = new HashMap<>();
    columns.put("string_column", Atom.string("A string"));
    columns.put("integer_column", Atom.integer(42));
    columns.put("bool_column", Atom.bool(true));
    columns.put("uuid_column", Atom.uuid(uuid));
    columns.put("named-uuid", Atom.namedUuid("uuid-name"));
    columns.put("map_column", Map.of(ImmutableMap.of("key", "value")));
    columns.put("set_column", Set.of("value1", "value2"));

    new EqualsTester().addEqualityGroup(row, new Row(columns)).testEquals();
  }
}
