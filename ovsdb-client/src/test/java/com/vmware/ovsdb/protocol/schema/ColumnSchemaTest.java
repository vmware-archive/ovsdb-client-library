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

import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import java.io.IOException;
import org.junit.Test;

public class ColumnSchemaTest {

    @Test
    public void testDeserialization() throws IOException {
        BaseType stringType = new StringBaseType(
            Atom.string("vxlan_over_ipv4"));
        ColumnSchema expectedResult = new ColumnSchema(
            new Type(stringType), null, false);

        String textSchema = "{\"type\":{\"key\":{\"type\":\"string\","
            + "\"enum\":\"vxlan_over_ipv4\"}},\"mutable\":false}";
        assertEquals(
            expectedResult,
            JsonUtil.deserialize(textSchema, ColumnSchema.class)
        );

        expectedResult = new ColumnSchema(
            new Type(new UuidBaseType("Logical_Switch", null)));
        textSchema = "{\"type\":{\"key\":{\"type\":\"uuid\","
            + "\"refTable\":\"Logical_Switch\"}}}";
        assertEquals(
            expectedResult,
            JsonUtil.deserialize(textSchema, ColumnSchema.class)
        );
    }

    @Test(expected = IOException.class)
    public void testInvalidJson() throws IOException {
        String textSchema = "{\"mutable\":false}";
        JsonUtil.deserialize(textSchema, ColumnSchema.class);
    }
}
