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
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import org.junit.Test;

public class SelectTest {

    @Test
    public void testSerialization1() throws JsonProcessingException {
        Select select = new Select("Logical_Switch")
            .where("name", Function.INCLUDES, "ls")
            .where("tunnel_key", Function.GREATER_THAN, 5001L)
            .columns("_uuid", "name", "other_config");
        String expectedResult
            = "{\"op\":\"select\",\"table\":\"Logical_Switch\","
            + "\"where\":[[\"name\",\"includes\",\"ls\"],"
            + "[\"tunnel_key\",\">\",5001]],"
            + "\"columns\":[\"_uuid\",\"name\",\"other_config\"]}";

        assertEquals(expectedResult, JsonUtil.serialize(select));
    }

    @Test
    public void testSerialization2() throws JsonProcessingException {
        Select select = new Select("Logical_Switch")
            .where("name", Function.INCLUDES, "ls")
            .where("tunnel_key", Function.GREATER_THAN, 5001L);
        String expectedResult
            = "{\"op\":\"select\",\"table\":\"Logical_Switch\","
            + "\"where\":[[\"name\",\"includes\",\"ls\"],"
            + "[\"tunnel_key\",\">\",5001]]}";

        assertEquals(expectedResult, JsonUtil.serialize(select));
    }
}
