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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.Pair;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import java.io.IOException;
import java.util.List;

public class MapDeserializer<K, V> extends StdDeserializer<Map<K, V>> {

    protected MapDeserializer() {
        this(null);
    }

    protected MapDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<K, V> deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        ArrayNode arrayNode = jp.getCodec().readTree(jp);
        if (arrayNode.size() != 2) {
            throw new IOException(
                "<map> should be a 2-element JSON array. Found "
                    + arrayNode.size() + " elements");
        }

        if (!OvsdbConstant.MAP.equals(arrayNode.get(0).asText())) {
            throw new IOException(
                "First element of <map> should be \"" + OvsdbConstant.MAP
                    + "\"");
        }

        List<Pair<K, V>> pairs = JsonUtil.treeToValueNoException(
            arrayNode.get(1), new TypeReference<List<Pair<K, V>>>() {
            }
        );
        return new Map<>(pairs);
    }
}
