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

import static com.vmware.ovsdb.protocol.schema.Constants.JSON_UUID;
import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class UuidBaseTypeTest {

  @Test
  public void testUuidDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.UUID);
    String jsonBaseType = JSON_UUID;

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test uuid type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_UUID + "}";
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test uuid base type with min and max
    jsonBaseType = "{\"type\":" + JSON_UUID
        + ", \"refTable\":\"Physical_Switch\", \"refType\":\"strong\"}";
    expectedResult = new UuidBaseType("Physical_Switch", "strong");
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test uuid base type with enum of set value
    jsonBaseType = "{\"type\":" + JSON_UUID
        + ", \"enum\":[\"set\", "
        + "[\"00000000-0000-0000-0000-000000000000\","
        + "\"00000000-0000-0000-0000-000000000001\","
        + "\"00000000-0000-0000-0000-000000000002\"]]}";

    Set enums = Set.of(
        "00000000-0000-0000-0000-000000000000",
        "00000000-0000-0000-0000-000000000001",
        "00000000-0000-0000-0000-000000000002"
    );
    expectedResult = new UuidBaseType(enums);

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

  @Test
  public void testEquals() {
    UUID uuid = UUID.randomUUID();
    new EqualsTester()
        .addEqualityGroup(new UuidBaseType(), new UuidBaseType(null, null))
        .addEqualityGroup(
            new UuidBaseType(Atom.uuid(uuid)), new UuidBaseType(new Atom<>(Uuid.of(uuid)))
        )
        .addEqualityGroup(new UuidBaseType("abc", null), new UuidBaseType("abc", null))
        .addEqualityGroup(new UuidBaseType("abc", "strong"), new UuidBaseType("abc", "strong"))
        .testEquals();
  }
}
