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

import static com.vmware.ovsdb.protocol.schema.Constants.JSON_REAL;
import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import org.junit.Test;

import java.io.IOException;

public class RealBaseTypeTest {

  @Test
  public void testDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.REAL);
    String jsonBaseType = JSON_REAL;

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test real type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_REAL + "}";
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test real base type with min and max
    jsonBaseType = "{\"type\":" + JSON_REAL
        + ", \"minReal\":1.5, \"maxReal\":100.11}";
    expectedResult = new RealBaseType(1.5, 100.11);

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test real base type with enum of single value
    jsonBaseType = "{\"type\":" + JSON_REAL + ", \"enum\":2.4}";
    expectedResult = new RealBaseType(Atom.real(2.4));

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test real base type with enum of set value
    jsonBaseType = "{\"type\":" + JSON_REAL
        + ", \"enum\":[\"set\", [1.3,2.7,3.9]]}";
    Set enums = Set.of(1.3, 2.7, 3.9);

    expectedResult = new RealBaseType(enums);
    assertEquals(expectedResult, JsonUtil.deserialize(jsonBaseType, BaseType.class));
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new RealBaseType(), new RealBaseType(null, null))
        .addEqualityGroup(new RealBaseType(Atom.real(4.2)), new RealBaseType(Atom.real(4.2)))
        .addEqualityGroup(new RealBaseType(2.4, 4.2), new RealBaseType(2.4, 4.2))
        .testEquals();
  }

}
