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

import static com.vmware.ovsdb.protocol.schema.Constants.JSON_BOOLEAN;
import static com.vmware.ovsdb.protocol.schema.Constants.JSON_INTEGER;
import static com.vmware.ovsdb.protocol.schema.Constants.JSON_REAL;
import static com.vmware.ovsdb.protocol.schema.Constants.JSON_STRING;
import static com.vmware.ovsdb.protocol.schema.Constants.JSON_UUID;
import static org.junit.Assert.assertEquals;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class AtomicTypeTest {

  @Test
  public void testDeserialization() throws JsonProcessingException {
    assertEquals(
        JSON_INTEGER, JsonUtil.serialize(AtomicType.INTEGER));
    assertEquals(
        JSON_REAL, JsonUtil.serialize(AtomicType.REAL));
    assertEquals(
        JSON_BOOLEAN, JsonUtil.serialize(AtomicType.BOOLEAN));
    assertEquals(
        JSON_STRING, JsonUtil.serialize(AtomicType.STRING));
    assertEquals(
        JSON_UUID, JsonUtil.serialize(AtomicType.UUID));
  }

  @Test
  public void testSerialization() throws IOException {
    assertEquals(
        AtomicType.INTEGER,
        JsonUtil.deserialize(JSON_INTEGER, AtomicType.class)
    );
    assertEquals(
        AtomicType.REAL,
        JsonUtil.deserialize(JSON_REAL, AtomicType.class)
    );
    assertEquals(
        AtomicType.BOOLEAN,
        JsonUtil.deserialize(JSON_BOOLEAN, AtomicType.class)
    );
    assertEquals(
        AtomicType.STRING,
        JsonUtil.deserialize(JSON_STRING, AtomicType.class)
    );
    assertEquals(
        AtomicType.UUID,
        JsonUtil.deserialize(JSON_UUID, AtomicType.class)
    );
  }
}
