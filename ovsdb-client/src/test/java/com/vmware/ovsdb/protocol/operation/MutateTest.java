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
import com.vmware.ovsdb.protocol.operation.notation.Mutation;
import com.vmware.ovsdb.protocol.operation.notation.Mutator;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class MutateTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    UUID uuid = UUID.randomUUID();

    Mutate mutate = new Mutate("Logical_Switch")
        .where("string_column", Function.INCLUDES, "A string")
        .where("integer_column", Function.LESS_THAN, 42)
        .where("bool_column", Function.EQUALS, true)
        .where("uuid_column", Function.NOT_EQUALS, Uuid.of(uuid))
        .where("named-uuid_column", Function.EXCLUDES, new NamedUuid("uuid-name"))
        .where("map_column", Function.GREATER_THAN_OR_EQUALS, ImmutableMap.of("key", "value"))
        .where("set_column", Function.LESS_THAN_OR_EQUALS, ImmutableSet.of("value1", "value2"))
        .mutation("column1", Mutator.SUM, 1)
        .mutation("column2", Mutator.DIFFERENCE, true)
        .mutation("column3", Mutator.PRODUCT, "abc")
        .mutation("column4", Mutator.QUOTIENT, Uuid.of(uuid))
        .mutation("column5", Mutator.REMINDER, new NamedUuid("uuid-name"))
        .mutation("column6", Mutator.INSERT, ImmutableMap.of("key", "value"))
        .mutation("column7", Mutator.DELETE, ImmutableSet.of("value1", "value2"));

    String expectedResult = "{\"op\":\"mutate\",\"table\":\"Logical_Switch\","
        + "\"mutations\":[[\"column1\",\"+=\",1],"
        + "[\"column2\",\"-=\",true],"
        + "[\"column3\",\"*=\",\"abc\"],"
        + "[\"column4\",\"/=\",[\"uuid\",\"" + uuid + "\"]],"
        + "[\"column5\",\"%=\",[\"named-uuid\",\"uuid-name\"]],"
        + "[\"column6\",\"insert\",[\"map\",[[\"key\",\"value\"]]]],"
        + "[\"column7\",\"delete\",[\"set\",[\"value2\",\"value1\"]]]"
        + "],"
        + "\"where\":[[\"string_column\",\"includes\",\"A string\"],"
        + "[\"integer_column\",\"<\",42],"
        + "[\"bool_column\",\"==\",true],"
        + "[\"uuid_column\",\"!=\",[\"uuid\",\"" + uuid + "\"]],"
        + "[\"named-uuid_column\",\"excludes\",[\"named-uuid\",\"uuid-name\"]],"
        + "[\"map_column\",\">=\",[\"map\",[[\"key\",\"value\"]]]],"
        + "[\"set_column\",\"<=\",[\"set\",[\"value2\",\"value1\"]]]"
        + "]}";

    assertEquals(expectedResult, JsonUtil.serialize(mutate));
  }

  @Test
  public void testEquals() {
    List<Condition> conditions = ImmutableList.of(
        new Condition("integer_column", Function.LESS_THAN, Atom.integer(42))
    );
    List<Mutation> mutations = ImmutableList.of(
        new Mutation("column3", Mutator.PRODUCT, Atom.string("abc"))
    );

    String tableName = "table";
    new EqualsTester()
        .addEqualityGroup(new Mutate(tableName, conditions, mutations),
            new Mutate(tableName)
                .where("integer_column", Function.LESS_THAN, 42)
                .mutation("column3", Mutator.PRODUCT, "abc")
        ).testEquals();
  }
}
