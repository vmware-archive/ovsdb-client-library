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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


import com.google.common.testing.EqualsTester;
import com.sun.org.apache.bcel.internal.generic.BASTORE;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class TypeTest {

  @Test
  public void testAtomicTypeDeserialization() throws IOException {
    Type expectedType = new Type(new IntegerBaseType());
    assertEquals(
        expectedType,
        JsonUtil.deserialize(JSON_INTEGER, Type.class)
    );

    expectedType = new Type(BaseType.atomicType(AtomicType.REAL));
    assertEquals(
        expectedType,
        JsonUtil.deserialize(JSON_REAL, Type.class)
    );

    expectedType = new Type(BaseType.atomicType(AtomicType.BOOLEAN));
    assertEquals(
        expectedType,
        JsonUtil.deserialize(JSON_BOOLEAN, Type.class)
    );

    expectedType = new Type(BaseType.atomicType(AtomicType.STRING));
    assertEquals(
        expectedType,
        JsonUtil.deserialize(JSON_STRING, Type.class)
    );

    expectedType = new Type(BaseType.atomicType(AtomicType.UUID));
    assertEquals(
        expectedType,
        JsonUtil.deserialize(JSON_UUID, Type.class)
    );
  }

  @Test
  public void testKeyOnlyDeserialization() throws IOException {
    Type expectedType = new Type(BaseType.atomicType(AtomicType.INTEGER));
    String textType = "{\"key\":\"integer\"}";
    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));

    textType = "{\"key\":\"integer\", \"min\":1, \"max\":100}";
    expectedType = new Type(
        BaseType.atomicType(AtomicType.INTEGER), null, 1L, 100L);
    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));

    textType = "{\"key\":\"integer\", \"min\":1, \"max\":\"unlimited\"}";
    expectedType = new Type(
        BaseType.atomicType(AtomicType.INTEGER), null, 1L, Long.MAX_VALUE);
    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));

    textType = "{\"key\":{\"type\":\"integer\",\"minInteger\":1, "
        + "\"maxInteger\":100}, \"min\":1, \"max\":\"unlimited\"}";
    expectedType = new Type(
        new IntegerBaseType(1L, 100L), null, 1L, Long.MAX_VALUE
    );

    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));
  }

  @Test
  public void testKeyValueDeserialization() throws IOException {
    BaseType key = BaseType.atomicType(AtomicType.INTEGER);
    BaseType value = BaseType.atomicType(AtomicType.REAL);
    Type expectedType = new Type(key, value, null, null);
    String textType = "{\"key\":\"integer\", \"value\":\"real\"}";
    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));

    key = BaseType.atomicType(AtomicType.INTEGER);
    value = BaseType.atomicType(AtomicType.STRING);
    expectedType = new Type(key, value, 1L, 100L);
    textType
        = "{\"key\":\"integer\", \"value\":\"string\",\"min\":1, "
        + "\"max\":100}";
    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));

    key = new IntegerBaseType(1L, 100L);
    value = new StringBaseType(10L, 20L);
    expectedType = new Type(key, value, 1L, Long.MAX_VALUE);
    textType = "{\"key\":{\"type\":\"integer\",\"minInteger\":1, "
        + "\"maxInteger\":100}, \"value\":{\"type\":\"string\","
        + "\"minLength\":10, \"maxLength\":20},\"min\":1, "
        + "\"max\":\"unlimited\"}";

    assertEquals(
        expectedType, JsonUtil.deserialize(textType, Type.class));
  }

  @Test
  public void testInvalidJsonDeserialization() {
    String invalidJson = "{\"value\":\"integer\"}";
    String errorMessage = "";
    try {
      JsonUtil.deserialize(invalidJson, Type.class);
    } catch (IOException e) {
      errorMessage = e.getMessage();
    }
    assertTrue(errorMessage.contains("\"key\" field is missing from <type>"));
  }

  @Test(expected = IOException.class)
  public void testInvalidMax() throws IOException {
    String invalidJson = "{\"key\":{\"type\":\"integer\",\"minInteger\":1, "
        + "\"maxInteger\":100}, \"value\":{\"type\":\"string\","
        + "\"minLength\":10, \"maxLength\":20},\"min\":1, "
        + "\"max\":\"limited\"}";
    JsonUtil.deserialize(invalidJson, Type.class);
  }

  @Test
  public void testEquals() {
    BaseType key = mock(BaseType.class);
    BaseType value = mock(BaseType.class);
    new EqualsTester()
        .addEqualityGroup(new Type(key), new Type(key, null, null, null))
        .addEqualityGroup(new Type(key, value, null, null), new Type(key, value, null, null))
        .addEqualityGroup(new Type(key, value, 12L, 34L), new Type(key, value, 12L, 34L))
        .testEquals();
  }
}
