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

import java.util.Objects;

/**
 * The response is a single object serialized using  JSON. It has three properties:
 * <p>
 *   result - The Object that was returned by the invoked method. This must be null in case there
 *   was an error invoking the method.
 *   error - An Error object if there was an error invoking the method. It must be null if there
 *   was no error.
 *   id - This must be the same id as the request it is responding to.
 * </p>
 *
 * @see <a href=http://www.jsonrpc.org/specification_v1>JSON-RPC 1.0 Specification</a>
 */
public class JsonRpcV1Response {

  private final JsonNode result;

  private final String error;

  private final String id;

  /**
   * Create a {@link JsonRpcV1Response} object.
   *
   * @param result RPC result as a {@link JsonNode} object. Should be null if there is an error
   * @param error RPC error. Should be null if there is no error
   * @param id response id that must be same as the request id
   */
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

  /**
   * Create a {@link JsonRpcV1Response} object.
   *
   * @param result RPC result as an {@link Object}. Should be null if there is an error
   * @param error RPC error. Should be null if there is no error
   * @param id response id that must be same as the request id
   */
  public JsonRpcV1Response(Object result, String error, String id) {
    this.result = JsonUtil.toJsonNode(result);
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
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof JsonRpcV1Response)) {
      return false;
    }
    JsonRpcV1Response that = (JsonRpcV1Response) other;
    return Objects.equals(result, that.result)
        && Objects.equals(error, that.error)
        && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, error, id);
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
