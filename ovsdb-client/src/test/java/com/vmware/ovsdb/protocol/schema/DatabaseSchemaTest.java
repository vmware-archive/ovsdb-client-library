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

import static org.junit.Assert.assertNotNull;

import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class DatabaseSchemaTest {

    @Test
    public void testVtepSchema() throws IOException {
        testDeserialization("/vtep_schema.json");
    }

    @Test
    public void testVswitchSchema() throws IOException {
        testDeserialization("/vswitch_schema.json");
    }

    private void testDeserialization(String schemaPath) throws IOException {
        // No exception means success
        DatabaseSchema dbSchema = JsonUtil.deserialize(
            getClass().getResource(schemaPath),
            DatabaseSchema.class
        );
        assertNotNull(dbSchema);
    }
}
