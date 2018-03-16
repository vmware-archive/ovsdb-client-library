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
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import java.io.IOException;

public class AtomDeserializer extends StdDeserializer<Atom> {

    protected AtomDeserializer() {
        this(null);
    }

    protected AtomDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Atom deserialize(
        JsonParser jp, DeserializationContext ctxt
    ) throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        if (jsonNode.isTextual()) {
            // <string>
            return Atom.string(jsonNode.asText());
        } else if (jsonNode.isIntegralNumber()) {
            // <integer>
            return Atom.integer(jsonNode.asLong());
        } else if (jsonNode.isDouble()) {
            // <real>
            return Atom.real(jsonNode.asDouble());
        } else if (jsonNode.isBoolean()) {
            // <boolean>
            return Atom.bool(jsonNode.asBoolean());
        } else if (jsonNode.isArray()) {
            // <uuid>
            Uuid uuid = JsonUtil.treeToValueNoException(jsonNode, Uuid.class);
            if (uuid != null) {
                return Atom.uuid(uuid);
            }
            // <named-uuid>
            NamedUuid namedUuid = JsonUtil.treeToValueNoException(jsonNode, NamedUuid.class);
            if (namedUuid != null) {
                return Atom.namedUuid(namedUuid);
            }
        }
        throw new IOException(jsonNode + " is not a valid <atom>");
    }
}
