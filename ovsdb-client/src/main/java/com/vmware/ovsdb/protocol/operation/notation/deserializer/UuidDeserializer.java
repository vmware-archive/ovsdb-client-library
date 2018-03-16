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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import java.io.IOException;
import java.util.UUID;

public class UuidDeserializer extends StdDeserializer<Uuid> {

    protected UuidDeserializer() {
        this(null);
    }

    protected UuidDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Uuid deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException, JsonProcessingException {
        ArrayNode arrayNode = jp.getCodec().readTree(jp);
        if (arrayNode.size() != 2) {
            throw new IOException(
                "<uuid> should be a 2-element JSON array. Found "
                    + arrayNode.size() + " elements");
        }

        if (!OvsdbConstant.UUID.equals(arrayNode.get(0).asText())) {
            throw new IOException(
                "First element of <uuid> should be \""
                    + OvsdbConstant.UUID + "\"");
        }

        String strUuid = arrayNode.get(1).asText();
        UUID uuid;
        try {
            uuid = UUID.fromString(strUuid);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid UUID " + strUuid);
        }
        return new Uuid(uuid);
    }
}
