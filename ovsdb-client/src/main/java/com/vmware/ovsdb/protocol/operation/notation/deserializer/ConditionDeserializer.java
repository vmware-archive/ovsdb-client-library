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

package com.vmware.ovsdb.protocol.operation.notation.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.io.IOException;

public class ConditionDeserializer extends StdDeserializer<Condition> {

  protected ConditionDeserializer() {
    this(null);
  }

  protected ConditionDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Condition deserialize(
      JsonParser jp, DeserializationContext ctxt
  ) throws IOException {
    ArrayNode arrayNode = jp.getCodec().readTree(jp);
    if (arrayNode.size() != 3) {
      throw new IOException(
          "<condition> should be a 3-element JSON array. Found "
              + arrayNode.size() + " elements");
    }
    String column = arrayNode.get(0).asText();
    String function = arrayNode.get(1).asText();
    Value value = JsonUtil.treeToValueNoException(arrayNode.get(2), Value.class);
    return new Condition(column, Function.fromString(function), value);
  }

}
