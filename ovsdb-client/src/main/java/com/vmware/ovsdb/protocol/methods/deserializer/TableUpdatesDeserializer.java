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

package com.vmware.ovsdb.protocol.methods.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vmware.ovsdb.protocol.methods.TableUpdate;
import com.vmware.ovsdb.protocol.methods.TableUpdates;

import java.io.IOException;
import java.util.Map;

public class TableUpdatesDeserializer extends StdDeserializer<TableUpdates> {

  protected TableUpdatesDeserializer() {
    this(null);
  }

  protected TableUpdatesDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public TableUpdates deserialize(
      JsonParser jp, DeserializationContext ctxt
  ) throws IOException {
    TypeReference<Map<String, TableUpdate>> typeRef =
        new TypeReference<Map<String, TableUpdate>>() {};
    Map<String, TableUpdate> tableUpdates = jp.readValueAs(typeRef);
    return new TableUpdates(tableUpdates);
  }
}
