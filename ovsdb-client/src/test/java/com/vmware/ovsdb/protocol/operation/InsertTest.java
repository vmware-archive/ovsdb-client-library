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
import static org.mockito.Mockito.mock;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import org.junit.Test;

public class InsertTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    Row row = new Row().stringColumn("name", "ls1")
        .stringColumn("description", "first logical switch");
    Insert insert = new Insert("Logical_Switch", row).withUuidName("insert_ls1");

    String expectedResult
        = "{\"op\":\"insert\",\"table\":\"Logical_Switch\","
        + "\"row\":{\"name\":\"ls1\",\"description\":\"first logical"
        + " switch\"}," + "\"uuid-name\":\"insert_ls1\"}";
    assertEquals(expectedResult, JsonUtil.serialize(insert));
  }

  @Test
  public void testEquals() {
    Row row = mock(Row.class);
    String tableName = "table";
    new EqualsTester()
        .addEqualityGroup(new Insert(tableName, row), new Insert(tableName, row, null))
        .addEqualityGroup(
            new Insert(tableName, row, "uuid-name"),
            new Insert(tableName, row).withUuidName("uuid-name")
        ).testEquals();
  }
}
