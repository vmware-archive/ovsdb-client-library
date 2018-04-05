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

package com.vmware.ovsdb.protocol.operation.notation;

import static org.junit.Assert.assertEquals;


import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.methods.RowUpdate;
import com.vmware.ovsdb.protocol.methods.TableUpdate;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;

public class TableUpdateTest {

  @Test
  public void testDeserialization() throws IOException {
    Row old1 = new Row(ImmutableMap.of(
        "name", Atom.string("ls1"),
        "description", Atom.string("First Logical Switch"),
        "tunnel_key", Atom.integer(5001)
    ));

    Row new1 = new Row(ImmutableMap.of(
        "name", Atom.string("ls2"),
        "description", Atom.string("Second Logical Switch"),
        "tunnel_key", Atom.integer(5002)
    ));

    Map<UUID, RowUpdate> rowUpdates = ImmutableMap.of(
        UUID.randomUUID(), new RowUpdate(old1, null),
        UUID.randomUUID(), new RowUpdate(null, new1)
    );
    TableUpdate expectedResult = new TableUpdate(rowUpdates);

    String textTableUpdate = JsonUtil.serialize(rowUpdates);

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(textTableUpdate, TableUpdate.class)
    );
  }
}
