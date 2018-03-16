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

package com.vmware.ovsdb.protocol.schema;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class TableSchemaTest {

    @Test
    public void testDeserialization() throws IOException {
        Type stringType = new Type(BaseType.atomicType(AtomicType.STRING));
        BaseType integerBaseType = BaseType.atomicType(AtomicType.INTEGER);
        Type tunnelKeyType = new Type(integerBaseType, null, 0L, null);
        Map<String, ColumnSchema> columns = ImmutableMap.of(
            "description", new ColumnSchema(stringType),
            "name", new ColumnSchema(stringType),
            "_uuid", new ColumnSchema(stringType),
            "tunnel_key", new ColumnSchema(tunnelKeyType)
        );
        TableSchema expectedResult = new TableSchema(columns, null, null,
            ImmutableList.of(ImmutableSet.of("name"))
        );

        String textTableSchema = "{\"columns\": {"
            + "\"description\": {\"type\": \"string\"},"
            + "\"name\": {\"type\": \"string\"},"
            + "\"_uuid\": {\"type\": \"string\"},"
            + "\"tunnel_key\": {"
            + "  \"type\": {"
            + "    \"key\": \"integer\","
            + "    \"min\": 0"
            + "  }"
            + "}}, \"indexes\":[[\"name\"]]}";
        assertEquals(
            expectedResult,
            JsonUtil.deserialize(textTableSchema, TableSchema.class)
        );
    }

    @Test
    public void testDeserialization2() throws IOException {
        BaseType uuidBaseType = new UuidBaseType("Manager", null);
        Type type1 = new Type(uuidBaseType, null, 0L, Long.MAX_VALUE);

        uuidBaseType = new UuidBaseType("Physical_Switch", null);
        Type type2 = new Type(uuidBaseType, null, 0L, Long.MAX_VALUE);

        Map<String, ColumnSchema> columns = ImmutableMap.of(
            "managers", new ColumnSchema(type1),
            "switches", new ColumnSchema(type2)
        );
        TableSchema expectedResult = new TableSchema(columns, 1L, true, null);

        String textTableSchema = "{"
            + "\"columns\": {"
            + "  \"managers\": {"
            + "    \"type\": {"
            + "      \"key\": {"
            + "        \"refTable\": \"Manager\","
            + "        \"type\": \"uuid\""
            + "      },"
            + "      \"max\": \"unlimited\","
            + "      \"min\": 0"
            + "    }"
            + "  },"
            + "  \"switches\": {"
            + "    \"type\": {"
            + "      \"key\": {"
            + "        \"refTable\": \"Physical_Switch\","
            + "        \"type\": \"uuid\""
            + "      },"
            + "      \"max\": \"unlimited\","
            + "      \"min\": 0"
            + "    }"
            + "  }"
            + "},"
            + "\"isRoot\": true,"
            + "\"maxRows\": 1"
            + "}";

        assertEquals(
            expectedResult,
            JsonUtil.deserialize(textTableSchema, TableSchema.class)
        );
    }

    @Test(expected = IOException.class)
    public void testInvalidJson() throws IOException {
        String invalidTableSchema = "{\"isRoot\":true, \"maxRows\":1}";
        JsonUtil.deserialize(invalidTableSchema, TableSchema.class);
    }
}
