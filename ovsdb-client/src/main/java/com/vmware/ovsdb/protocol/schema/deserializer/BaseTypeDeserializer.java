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

package com.vmware.ovsdb.protocol.schema.deserializer;

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.ENUM;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MAX_INTEGER;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MAX_LENGTH;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MAX_REAL;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MIN_INTEGER;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MIN_LENGTH;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MIN_REAL;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.REF_TABLE;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.REF_TYPE;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.TYPE;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Value;
import com.vmware.ovsdb.protocol.schema.AtomicType;
import com.vmware.ovsdb.protocol.schema.BaseType;
import com.vmware.ovsdb.protocol.schema.BooleanBaseType;
import com.vmware.ovsdb.protocol.schema.IntegerBaseType;
import com.vmware.ovsdb.protocol.schema.RealBaseType;
import com.vmware.ovsdb.protocol.schema.StringBaseType;
import com.vmware.ovsdb.protocol.schema.UuidBaseType;

import java.io.IOException;

public class BaseTypeDeserializer extends StdDeserializer<BaseType> {

  protected BaseTypeDeserializer() {
    this(null);
  }

  protected BaseTypeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public BaseType deserialize(
      JsonParser jp, DeserializationContext ctxt
  ) throws IOException {
    JsonNode jsonNode = jp.getCodec().readTree(jp);
    AtomicType atomicType = JsonUtil.treeToValueNoException(jsonNode, AtomicType.class);
    // This <base-type> is a <atomic-type>, just return
    if (atomicType != null) {
      return BaseType.atomicType(atomicType);
    }

    // Not an <atomic-type>
    JsonNode typeNode = jsonNode.get(TYPE);
    if (typeNode == null) {
      throw new IOException(
          "\"" + TYPE + "\" field is missing from <base-type>: " + jsonNode);
    }
    atomicType = JsonUtil.treeToValue(typeNode, AtomicType.class);
    switch (atomicType) {
      case INTEGER:
        return deserializeIntegerType(jsonNode);
      case REAL:
        return deserializeRealType(jsonNode);
      case BOOLEAN:
        return deserializeBooleanType(jsonNode);
      case STRING:
        return deserializeStringType(jsonNode);
      case UUID:
      default:
        return deserializeUuidType(jsonNode);
    }
  }

  private IntegerBaseType deserializeIntegerType(JsonNode jsonNode)
      throws IOException {
    JsonNode enumNode = jsonNode.get(ENUM);
    if (enumNode != null) {
      return new IntegerBaseType(JsonUtil.treeToValue(enumNode, Value.class));
    } else {
      Long minInteger = null;
      Long maxInteger = null;

      JsonNode minIntegerNode = jsonNode.get(MIN_INTEGER);
      if (minIntegerNode != null) {
        minInteger = minIntegerNode.asLong();
      }

      JsonNode maxIntegerNode = jsonNode.get(MAX_INTEGER);
      if (maxIntegerNode != null) {
        maxInteger = maxIntegerNode.asLong();
      }
      return new IntegerBaseType(minInteger, maxInteger);
    }
  }

  private RealBaseType deserializeRealType(JsonNode jsonNode)
      throws IOException {
    JsonNode enumNode = jsonNode.get(ENUM);
    if (enumNode != null) {
      return new RealBaseType(JsonUtil.treeToValue(enumNode, Value.class));
    } else {
      Double minReal = null;
      Double maxReal = null;

      JsonNode minRealNode = jsonNode.get(MIN_REAL);
      if (minRealNode != null) {
        minReal = minRealNode.asDouble();
      }

      JsonNode maxRealNode = jsonNode.get(MAX_REAL);
      if (maxRealNode != null) {
        maxReal = maxRealNode.asDouble();
      }

      return new RealBaseType(minReal, maxReal);
    }
  }

  private BooleanBaseType deserializeBooleanType(JsonNode jsonNode)
      throws IOException {
    JsonNode enumNode = jsonNode.get(ENUM);
    if (enumNode != null) {
      return new BooleanBaseType(JsonUtil.treeToValue(enumNode, Value.class));
    }
    return new BooleanBaseType();
  }

  private StringBaseType deserializeStringType(JsonNode jsonNode)
      throws IOException {
    JsonNode enumNode = jsonNode.get(ENUM);
    if (enumNode != null) {
      return new StringBaseType(JsonUtil.treeToValue(enumNode, Value.class));
    } else {
      Long minLength = null;
      Long maxLength = null;

      JsonNode minLengthNode = jsonNode.get(MIN_LENGTH);
      if (minLengthNode != null) {
        minLength = minLengthNode.asLong();
      }

      JsonNode maxLengthNode = jsonNode.get(MAX_LENGTH);
      if (maxLengthNode != null) {
        maxLength = maxLengthNode.asLong();
      }

      return new StringBaseType(minLength, maxLength);
    }
  }

  private UuidBaseType deserializeUuidType(JsonNode jsonNode)
      throws IOException {
    JsonNode enumNode = jsonNode.get(ENUM);
    if (enumNode != null) {
      return new UuidBaseType(JsonUtil.treeToValue(enumNode, Value.class));
    } else {
      String refTable = null;
      String refType = null;

      JsonNode refTableNode = jsonNode.get(REF_TABLE);
      if (refTableNode != null) {
        refTable = refTableNode.asText();
      }

      JsonNode refTypeNode = jsonNode.get(REF_TYPE);
      if (refTypeNode != null) {
        refType = refTypeNode.asText();
      }

      return new UuidBaseType(refTable, refType);
    }
  }
}
