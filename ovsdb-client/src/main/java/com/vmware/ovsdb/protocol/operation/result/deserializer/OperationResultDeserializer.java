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

package com.vmware.ovsdb.protocol.operation.result.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.result.EmptyResult;
import com.vmware.ovsdb.protocol.operation.result.ErrorResult;
import com.vmware.ovsdb.protocol.operation.result.InsertResult;
import com.vmware.ovsdb.protocol.operation.result.OperationResult;
import com.vmware.ovsdb.protocol.operation.result.SelectResult;
import com.vmware.ovsdb.protocol.operation.result.UpdateResult;
import java.io.IOException;
import java.util.List;

public class OperationResultDeserializer extends StdDeserializer<OperationResult> {

    protected OperationResultDeserializer() {
        this(null);
    }

    protected OperationResultDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OperationResult deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        if (jsonNode.size() == 0) {
            return new EmptyResult();
        } else if (jsonNode.size() == 1) {
            // insert result
            JsonNode result = jsonNode.get("uuid");
            if (result != null) {
                return new InsertResult(JsonUtil.treeToValueNoException(result, Uuid.class));
            }
            // select result
            result = jsonNode.get("rows");
            if (result != null) {
                return new SelectResult(
                    JsonUtil.treeToValueNoException(result, new TypeReference<List<Row>>() {}));
            }
            // update/mutate/delete result
            result = jsonNode.get("count");
            if (result != null) {
                return new UpdateResult(result.asLong());
            }
            // error
            result = jsonNode.get("error");
            if (result != null) {
                return new ErrorResult(result.asText(), null);
            }
        } else if (jsonNode.size() == 2) {
            JsonNode errorNode = jsonNode.get("error");
            JsonNode detailsNode = jsonNode.get("details");
            if (errorNode != null && detailsNode != null) {
                return new ErrorResult(errorNode.asText(), detailsNode.asText());
            }
        }
        throw new IOException(jsonNode + " is not a valid operation result");
    }
}
