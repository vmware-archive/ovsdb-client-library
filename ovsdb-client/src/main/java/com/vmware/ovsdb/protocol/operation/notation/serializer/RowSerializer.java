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

package com.vmware.ovsdb.protocol.operation.notation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vmware.ovsdb.protocol.operation.notation.Row;

import java.io.IOException;

public class RowSerializer extends StdSerializer<Row> {

  public RowSerializer() {
    this(null);
  }

  protected RowSerializer(Class<Row> klass) {
    super(klass);
  }

  @Override
  public void serialize(
      Row row, JsonGenerator jgen, SerializerProvider provider
  ) throws IOException {
    jgen.writeObject(row.getColumns());
  }
}
