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
import static org.junit.Assert.assertNotNull;


import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import java.io.IOException;
import org.junit.Test;

public class BaseTypeTest {

  @Test
  public void testIntegerDeserialization() throws IOException {
    // Test atomic type as base type
    BaseType expectedResult = BaseType.atomicType(AtomicType.INTEGER);
    String jsonBaseType = JSON_INTEGER;

    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

    // Test base type that has only "type" field
    jsonBaseType = "{\"type\":" + JSON_INTEGER + "}";
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );

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
  public void testRealDeserialization() throws IOException {
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
    Set enums = Set.of(1L, 2L, 3L);

    expectedResult = new RealBaseType(enums);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

  @Test
  public void testBooleanDeserialization() throws IOException {
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
    jsonBaseType = "{\"type\":" + JSON_BOOLEAN
        + ", \"enum\":[\"set\", [true, false]]}";
    Set enums = Set.of(true, false);
    expectedResult = new BooleanBaseType(enums);
    assertEquals(
        expectedResult,
        JsonUtil.deserialize(jsonBaseType, BaseType.class)
    );
  }

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
  public void testInvalidJson() {

    // Test incorrect type field
    String jsonBaseType = "{\"type\": \"double\"}";
    Exception exception = null;
    try {
      JsonUtil.deserialize(jsonBaseType, BaseType.class);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);

    // Test missing type field
    jsonBaseType = "{\"minInteger\": 0, \"maxInteger\":1}";
    exception = null;
    try {
      JsonUtil.deserialize(jsonBaseType, BaseType.class);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);

    // Test incorrect enum field
    jsonBaseType = "{\"type\":" + JSON_INTEGER
        + ", \"enum\":[1,2,3]}";
    exception = null;
    try {
      JsonUtil.deserialize(jsonBaseType, BaseType.class);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
  }
}
