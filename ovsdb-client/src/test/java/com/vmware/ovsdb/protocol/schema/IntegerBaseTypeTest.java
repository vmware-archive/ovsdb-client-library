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

import static com.vmware.ovsdb.protocol.schema.Constants.JSON_INTEGER;
import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import org.junit.Test;

import java.io.IOException;

public class IntegerBaseTypeTest {

  @Test
  public void testDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.INTEGER);
    String jsonBaseType = JSON_INTEGER;

    assertEquals(expectedResult, JsonUtil.deserialize(jsonBaseType, BaseType.class));

    // Test base type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_INTEGER + "}";
    assertEquals(expectedResult, JsonUtil.deserialize(jsonBaseType, BaseType.class));

    // Test integer base type with min and max
    jsonBaseType = "{\"type\":" + JSON_INTEGER
        + ", \"minInteger\":0, \"maxInteger\":100}";
    expectedResult = new IntegerBaseType(0L, 100L);

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test integer base type with enum of single value
    jsonBaseType = "{\"type\":" + JSON_INTEGER + ", \"enum\":1}";
    expectedResult = new IntegerBaseType(Atom.integer(1));
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test integer base type with enum of set value
    jsonBaseType = "{\"type\":" + JSON_INTEGER
        + ", \"enum\":[\"set\", [1,2,3]]}";
    Set enums = Set.of(1L, 2L, 3L);

    expectedResult = new IntegerBaseType(enums);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new IntegerBaseType(), new IntegerBaseType(null, null))
        .addEqualityGroup(new IntegerBaseType(2L, 4L), new IntegerBaseType(2L, 4L))
        .addEqualityGroup(
            new IntegerBaseType(Atom.integer(42)), new IntegerBaseType(Atom.integer(42))
        )
        .addEqualityGroup(new IntegerBaseType(2L, null), new IntegerBaseType(2L, null))
        .addEqualityGroup(new IntegerBaseType(null, 4L), new IntegerBaseType(null, 4L))
        .testEquals();
  }

}
