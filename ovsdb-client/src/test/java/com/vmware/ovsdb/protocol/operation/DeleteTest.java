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

package com.vmware.ovsdb.protocol.operation;

import static org.junit.Assert.assertEquals;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Pair;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteTest {

  private final UUID uuid = UUID.randomUUID();

  private final Delete delete = new Delete("Logical_Switch")
      .where("string_column", Function.INCLUDES, "A string")
      .where("integer_column", Function.LESS_THAN, 42)
      .where("bool_column", Function.EQUALS, true)
      .where("uuid_column", Function.NOT_EQUALS, Uuid.of(uuid))
      .where("named-uuid_column", Function.EXCLUDES, new NamedUuid("uuid-name"))
      .where("map_column", Function.GREATER_THAN_OR_EQUALS, ImmutableMap.of("key", "value"))
      .where("set_column", Function.LESS_THAN_OR_EQUALS, ImmutableSet.of("value1", "value2"));

  @Test
  public void testSerialization() throws JsonProcessingException {
    String expectedResult = "{\"op\":\"delete\",\"table\":\"Logical_Switch\","
        + "\"where\":[[\"string_column\",\"includes\",\"A string\"],"
        + "[\"integer_column\",\"<\",42],"
        + "[\"bool_column\",\"==\",true],"
        + "[\"uuid_column\",\"!=\",[\"uuid\",\"" + uuid + "\"]],"
        + "[\"named-uuid_column\",\"excludes\",[\"named-uuid\",\"uuid-name\"]],"
        + "[\"map_column\",\">=\",[\"map\",[[\"key\",\"value\"]]]],"
        + "[\"set_column\",\"<=\",[\"set\",[\"value2\",\"value1\"]]]"
        + "]"
        + "}";

    assertEquals(expectedResult, JsonUtil.serialize(delete));
  }

  @Test
  public void testEquals() {
    List<Condition> conditions = Stream.of(
        new Condition("string_column", Function.INCLUDES, Atom.string("A string")),
        new Condition("integer_column", Function.LESS_THAN, Atom.integer(42)),
        new Condition("bool_column", Function.EQUALS, Atom.bool(true)),
        new Condition("uuid_column", Function.NOT_EQUALS, Atom.uuid(uuid)),
        new Condition("named-uuid_column", Function.EXCLUDES, Atom.namedUuid("uuid-name")),
        new Condition("map_column", Function.GREATER_THAN_OR_EQUALS,
            new Map<>(ImmutableList.of(new Pair<>(Atom.string("key"), Atom.string("value"))))
        ),
        new Condition("set_column", Function.LESS_THAN_OR_EQUALS,
            new Set(ImmutableSet.of(Atom.string("value1"), Atom.string("value2")))
        )
    ).collect(Collectors.toList());
    new EqualsTester().addEqualityGroup(delete, new Delete("Logical_Switch", conditions)).testEquals();
  }
}
