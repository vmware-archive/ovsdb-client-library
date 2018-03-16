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
package com.vmware.ovsdb.jsonrpc.v1.model;

import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.ERROR;
import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.ID;
import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.RESULT;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;

/**
 * When the method invocation completes, the service must reply with a response. The response is a
 * single object serialized using  JSON. <p> It has three properties: <p> result - The Object that
 * was returned by the invoked method. This must be null in case there was an error invoking the
 * method. error - An Error object if there was an error invoking the method. It must be null if
 * there was no error. id - This must be the same id as the request it is responding to.
 *
 * @see <a href=http://www.jsonrpc.org/specification_v1>JSON-RPC 1.0 Specification</a>
 */
public class JsonRpcV1Response {

    private final JsonNode result;

    private final String error;

    private final String id;

    @JsonCreator
    public JsonRpcV1Response(
        @JsonProperty(value = RESULT, required = true) JsonNode result,
        @JsonProperty(value = ERROR, required = true) String error,
        @JsonProperty(value = ID, required = true) String id
    ) {
        this.result = result;
        this.error = error;
        this.id = id;
    }

    public JsonRpcV1Response(Object result, String error, String id) {
        this.result = JsonUtil.toJsonNode(result);
        ;
        this.error = error;
        this.id = id;
    }

    public JsonNode getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "result=" + result
            + ", error=" + error
            + ", id=" + id
            + "]";
    }
}
