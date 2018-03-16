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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import java.io.IOException;

public class SetDeserializer extends StdDeserializer<Set> {

    protected SetDeserializer() {
        this(null);
    }

    protected SetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        // An <atom> that represents a set with exactly one element
        if (jsonNode.isValueNode()) {
            JsonUtil.treeToValue(jsonNode, Atom.class);
            return Set.of(JsonUtil.treeToValue(jsonNode, Atom.class));
        }
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        if (arrayNode.size() != 2) {
            throw new IOException(
                "<set> should be a 2-element JSON array. Found "
                    + arrayNode.size() + " elements");
        }

        if (!OvsdbConstant.SET.equals(arrayNode.get(0).asText())) {
            throw new IOException(
                "First element of <set> should be \"" + OvsdbConstant.SET
                    + "\"");
        }

        java.util.Set<Atom> atoms = JsonUtil.treeToValueNoException(
            arrayNode.get(1),
            new TypeReference<java.util.Set<Atom>>() {
            }
        );
        return new Set(atoms);
    }
}
