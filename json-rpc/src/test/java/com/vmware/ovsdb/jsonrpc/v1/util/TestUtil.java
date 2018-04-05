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

package com.vmware.ovsdb.jsonrpc.v1.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Response;

public class TestUtil {

  public static JsonNode getRequestNode(String id, String method, Object... params) {
    return JsonUtil.toJsonNode(new JsonRpcV1Request(id, method, params));
  }

  public static JsonNode getResponseNode(String id, Object result, String error) {
    return JsonUtil.toJsonNode(new JsonRpcV1Response(result, error, id));
  }
}
