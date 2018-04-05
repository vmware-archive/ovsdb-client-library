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
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import java.util.List;
import org.junit.Test;

public class WaitTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    int timeout = 1000;
    List<Condition> where = ImmutableList.of(
        new Condition("name", Function.EQUALS, Atom.string("ls1"))
    );
    List<String> columns = ImmutableList.of("name", "description");
    List<Row> rows = ImmutableList.of(
        new Row().column("name", Atom.string("ls1"))
            .column("description", Atom.string("First Logical Switch"))
    );
    Wait wait = new Wait(
        "Logical_Switch", timeout, where, columns, "==", rows);

    String expectedResult
        = "{\"op\":\"wait\",\"timeout\":1000,\"table\":\"Logical_Switch\","
        + "\"where\":[[\"name\",\"==\",\"ls1\"]],"
        + "\"columns\":[\"name\",\"description\"],\"until\":\"==\","
        + "\"rows\":[{\"name\":\"ls1\",\"description\":\"First Logical "
        + "Switch\"}]}";

    assertEquals(expectedResult, JsonUtil.serialize(wait));
  }
}
