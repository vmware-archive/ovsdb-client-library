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

import static com.vmware.ovsdb.protocol.schema.Constants.JSON_STRING;
import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import org.junit.Test;

import java.io.IOException;

public class StringBaseTypeTest {

  @Test
  public void testStringDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.STRING);
    String jsonBaseType = JSON_STRING;
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test string type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_STRING + "}";
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test string base type with min and max
    jsonBaseType = "{\"type\":" + JSON_STRING
        + ", \"minLength\":1, \"maxLength\":100}";
    expectedResult = new StringBaseType(1L, 100L);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test string base type with enum of single value
    jsonBaseType = "{\"type\":" + JSON_STRING + ", \"enum\":\"Holiday\"}";
    expectedResult = new StringBaseType(Atom.string("Holiday"));
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test string base type with enum of set value
    jsonBaseType = "{\"type\":" + JSON_STRING
        + ", \"enum\":[\"set\", [\"Monday\",\"Tuesday\",\"Wednesday\"]]}";
    Set enums = Set.of("Monday", "Tuesday", "Wednesday");
    expectedResult = new StringBaseType(enums);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new StringBaseType(), new StringBaseType(null, null))
        .addEqualityGroup(
            new StringBaseType(Atom.string("123")), new StringBaseType(Atom.string("123"))
        )
        .addEqualityGroup(new StringBaseType(24L, null), new StringBaseType(24L, null))
        .addEqualityGroup(new StringBaseType(null, 42L), new StringBaseType(null, 42L))
        .testEquals();
  }
}
