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

package com.vmware.ovsdb.protocol.operation.result;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import java.io.IOException;
import java.util.UUID;
import org.junit.Test;

public class OperationResultTest {

    @Test
    public void testInsertResultDeserialization() throws IOException {
        String jsonString
            = "{\"uuid\":[\"uuid\","
            + "\"00000000-0000-0000-0000-000000000000\"]}";

        Uuid uuid = new Uuid(
            UUID.fromString("00000000-0000-0000-0000-000000000000"));
        InsertResult expectedResult = new InsertResult(uuid);

        assertEquals(
            expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
        );
    }

    @Test
    public void testSelectResultDeserialization() throws IOException {
        String jsonString
            = "{\"rows\":["
            + "{\"_uuid\":[\"uuid\","
            + "\"00000000-0000-0000-0000-000000000000\"]"
            + ",\"name\":\"ls1\",\"description\":\"first logical switch\","
            + "\"tunnel_key\":5001},"
            + "{\"_uuid\":[\"uuid\","
            + "\"00000000-0000-0000-0000-000000000001\"],"
            + "\"name\":\"ls2\",\"description\":\"second logical switch\","
            + "\"tunnel_key\":5002}]}";

        Row row1 = new Row(
            ImmutableMap.of(
                "_uuid", Atom.uuid(
                    UUID.fromString("00000000-0000-0000-0000-000000000000")),
                "name", Atom.string("ls1"),
                "description", Atom.string("first logical switch"),
                "tunnel_key", Atom.integer(5001)
            )
        );

        Row row2 = new Row(
            ImmutableMap.of(
                "_uuid", Atom.uuid(
                    UUID.fromString("00000000-0000-0000-0000-000000000001")),
                "name", Atom.string("ls2"),
                "description", Atom.string("second logical switch"),
                "tunnel_key", Atom.integer(5002)
            )
        );

        SelectResult expectedResult = new SelectResult(ImmutableList.of(row1, row2));

        assertEquals(
            expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
        );
    }

    @Test
    public void testUpdateResultDeserialization() throws IOException {
        String jsonString = "{\"count\":2}";

        UpdateResult expectedResult = new UpdateResult(2);

        assertEquals(
            expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
        );
    }

    @Test
    public void testErrorResultDeserialization() {
        String jsonString = "{\"error\":\"constraint violation\", "
            + "\"details\":\"duplicate name\"}";

        ErrorResult expectedResult = new ErrorResult("constraint violation", "duplicate name");

        assertEquals(
            expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
        );

        jsonString = "{\"error\":\"constraint violation\"}";

        expectedResult = new ErrorResult("constraint violation", null);

        assertEquals(
            expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
        );
    }

    @Test
    public void testEmptyResultDeserialization() throws IOException {
        String jsonString = "{}";

        EmptyResult expectedResult = new EmptyResult();

        assertEquals(
            expectedResult,
            JsonUtil.deserializeNoException(
                jsonString, OperationResult.class)
        );
    }
}
