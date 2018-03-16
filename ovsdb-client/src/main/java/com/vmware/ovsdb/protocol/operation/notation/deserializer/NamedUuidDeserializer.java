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
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import java.io.IOException;

public class NamedUuidDeserializer extends StdDeserializer<NamedUuid> {

    protected NamedUuidDeserializer() {
        this(null);
    }

    protected NamedUuidDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public NamedUuid deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        ArrayNode arrayNode = jp.getCodec().readTree(jp);
        if (arrayNode.size() != 2) {
            throw new IOException(
                "<named-uuid> should be a 2-element JSON array. Found "
                    + arrayNode.size() + " elements");
        }

        if (!OvsdbConstant.NAMED_UUID.equals(arrayNode.get(0).asText())) {
            throw new IOException(
                "First element of <named-uuid> should be \""
                    + OvsdbConstant.NAMED_UUID + "\"");
        }

        String uuidName = arrayNode.get(1).asText();
        return new NamedUuid(uuidName);
    }
}
