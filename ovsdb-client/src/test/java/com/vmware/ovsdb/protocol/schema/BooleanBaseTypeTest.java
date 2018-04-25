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
import static org.junit.Assert.assertEquals;

import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import org.junit.Test;

import java.io.IOException;

public class BooleanBaseTypeTest {

  @Test
  public void testDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.BOOLEAN);
    String jsonBaseType = JSON_BOOLEAN;

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test boolean type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_BOOLEAN + "}";
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test boolean base type with enum of single value
    jsonBaseType = "{\"type\":" + JSON_BOOLEAN + ", \"enum\":false}";
    expectedResult = new BooleanBaseType(Atom.bool(false));
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test boolean base type with enum of set value
    jsonBaseType = "{\"type\":" + JSON_BOOLEAN + ", \"enum\":[\"set\", [true, false]]}";
    Set enums = Set.of(true, false);
    expectedResult = new BooleanBaseType(enums);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

  @Test
  public void testEquals() {
    new EqualsTester().addEqualityGroup(new BooleanBaseType(), new BooleanBaseType()).testEquals();
  }
}
