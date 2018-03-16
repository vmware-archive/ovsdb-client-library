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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.KEY;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MAX;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MIN;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.UNLIMITED;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.VALUE;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.schema.AtomicType;
import com.vmware.ovsdb.protocol.schema.BaseType;
import com.vmware.ovsdb.protocol.schema.Type;
import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<Type> {

    protected TypeDeserializer() {
        this(null);
    }

    protected TypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Type deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        AtomicType atomicType = JsonUtil.treeToValueNoException(jsonNode, AtomicType.class);
        // This <base-type> is a <atomic-type>, just return
        if (atomicType != null) {
            return new Type(BaseType.atomicType(atomicType));
        }
        JsonNode keyNode = jsonNode.get(KEY);
        if (keyNode == null) {
            throw new IOException(
                "\"" + KEY + "\" field is missing from <type>: " + jsonNode);
        }

        BaseType key = JsonUtil.treeToValue(keyNode, BaseType.class);
        BaseType value = null;
        Long min = null;
        Long max = null;

        JsonNode valueNode = jsonNode.get(VALUE);
        if (valueNode != null) {
            value = JsonUtil.treeToValue(valueNode, BaseType.class);
        }
        JsonNode minNode = jsonNode.get(MIN);
        if (minNode != null) {
            min = minNode.asLong();
        }
        JsonNode maxNode = jsonNode.get(MAX);
        if (maxNode != null) {
            if (maxNode.isTextual()) {
                if (!UNLIMITED.equals(maxNode.asText())) {
                    throw new IOException(
                        "Invalid \"" + MAX + "\" value: " + maxNode + " of <type>: " + jsonNode);
                }
                max = Long.MAX_VALUE;
            } else {
                max = maxNode.asLong();
            }
        }

        return new Type(key, value, min, max);
    }
}
