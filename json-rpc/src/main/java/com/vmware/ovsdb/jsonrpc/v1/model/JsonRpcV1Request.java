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

import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.ID;
import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.METHOD;
import static com.vmware.ovsdb.jsonrpc.v1.util.JsonRpcConstant.PARAMS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;

/**
 * A remote methods is invoked by sending a request to a remote service. The request is a single
 * object serialized using  JSON. <p> It has three properties: <p> methods - A String containing the
 * name of the methods to be invoked. params - An Array of objects to pass as arguments to the
 * methods. id - The request id. This can be of any type. It is used to match the response with the
 * request that it is replying to.
 *
 * @see <a href=http://www.jsonrpc.org/specification_v1>JSON-RPC 1.0 Specification</a>
 */
public class JsonRpcV1Request {

    private final String method;

    private final ArrayNode params;

    private final String id;

    @JsonCreator
    public JsonRpcV1Request(
        @JsonProperty(value = METHOD, required = true) String method,
        @JsonProperty(value = PARAMS, required = true) ArrayNode params,
        @JsonProperty(value = ID, required = true) String id
    ) {
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public JsonRpcV1Request(String id, String method, Object... params) {
        this.method = method;
        this.params = JsonUtil.createArrayNode();
        for (Object param : params) {
            this.params.add(JsonUtil.toJsonNode(param));
        }
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public ArrayNode getParams() {
        return params;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "methods=" + method
            + ", params=" + params
            + ", id=" + id
            + "]";
    }
}
