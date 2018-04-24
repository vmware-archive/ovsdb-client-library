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
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.notation.Value;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class SelectResultTest {

  private final Map<String, Value> columns1 = ImmutableMap.of(
      "_uuid", Atom.uuid(
          UUID.fromString("00000000-0000-0000-0000-000000000000")),
      "name", Atom.string("ls1"),
      "description", Atom.string("first logical switch"),
      "tunnel_key", Atom.integer(5001)
  );

  private final Map<String, Value> columns2 = ImmutableMap.of(
      "_uuid", Atom.uuid(
          UUID.fromString("00000000-0000-0000-0000-000000000001")),
      "name", Atom.string("ls2"),
      "description", Atom.string("second logical switch"),
      "tunnel_key", Atom.integer(5002)
  );

  @Test
  public void testDeserialization() {
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

    Row row1 = new Row(columns1);
    Row row2 = new Row(columns2);

    SelectResult expectedResult = new SelectResult(ImmutableList.of(row1, row2));

    assertEquals(
        expectedResult, JsonUtil.deserializeNoException(jsonString, OperationResult.class)
    );
  }

  @Test
  public void testEquals() {
    Map<String, Value> columns3 = new HashMap<>(columns1);
    Map<String, Value> columns4 = new TreeMap<>(columns2);
    new EqualsTester()
        .addEqualityGroup(new SelectResult(ImmutableList.of(new Row(columns1), new Row(columns2))),
            new SelectResult(ImmutableList.of(new Row(columns3), new Row(columns4))))
        .testEquals();
  }
}
