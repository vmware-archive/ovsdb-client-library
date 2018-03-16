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
import com.vmware.ovsdb.protocol.methods.RowUpdate;
import com.vmware.ovsdb.protocol.methods.TableUpdate;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class TableUpdateDeserializer extends StdDeserializer<TableUpdate> {

    protected TableUpdateDeserializer() {
        this(null);
    }

    protected TableUpdateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public TableUpdate deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        TypeReference<Map<UUID, RowUpdate>> typeRef
            = new TypeReference<Map<UUID, RowUpdate>>() {
        };
        Map<UUID, RowUpdate> rowUpdates = jp.readValueAs(typeRef);
        return new TableUpdate(rowUpdates);
    }
}
