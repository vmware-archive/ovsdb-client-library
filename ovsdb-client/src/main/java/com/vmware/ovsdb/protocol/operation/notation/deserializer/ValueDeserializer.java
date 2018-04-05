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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.io.IOException;

public class ValueDeserializer extends StdDeserializer<Value> {

  protected ValueDeserializer() {
    this(null);
  }

  protected ValueDeserializer(Class<? extends Value> vc) {
    super(vc);
  }

  @Override
  public Value deserialize(
      JsonParser jp, DeserializationContext ctxt
  ) throws IOException {
    JsonNode jsonNode = jp.getCodec().readTree(jp);
    Atom atom = JsonUtil.treeToValueNoException(jsonNode, Atom.class);
    if (atom != null) {
      return atom;
    }
    Set set = JsonUtil.treeToValueNoException(jsonNode, Set.class);
    if (set != null) {
      return set;
    }
    Map map = JsonUtil.treeToValueNoException(jsonNode, Map.class);
    if (map != null) {
      return map;
    }
    throw new IOException(jsonNode + " is not a valid <value>");
  }
}
