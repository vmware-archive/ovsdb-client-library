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

package com.vmware.ovsdb.jsonrpc.v1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for a JSON-RPC 1.0 client, with which a user can send request and notification to a
 * remote JSON-RPC server.
 */
public interface JsonRpcV1Client {

  /**
   * Send a JSON-RPC request. The id must be unique among all calls.
   *
   * @param id a unique ID for the call
   * @param method method name
   * @param returnType return type of the method
   * @param params params of the method
   * @return a {@link CompletableFuture} to get the method result from
   * @throws JsonRpcException if fail to send the request
   * @see <a href=http://www.jsonrpc.org/specification_v1>JSON-RPC 1.0 Specification</a>
   */
  <T> CompletableFuture<T> call(String id, String method, Class<T> returnType, Object... params)
      throws JsonRpcException;

  /**
   * Send a JSON-RPC notification.
   *
   * @param method method name
   * @param params params of the method
   * @throws JsonRpcException if fail to send the request
   * @see <a href=http://www.jsonrpc.org/specification_v1>JSON-RPC 1.0 Specification</a>
   */
  void notify(String method, Object... params) throws JsonRpcException;

  /**
   * Handle a response. This method should be called by the transporter when a response is
   * received.
   *
   * @param responseNode the response {@link JsonNode}
   */
  void handleResponse(JsonNode responseNode) throws JsonRpcException;

  /**
   * Shutdown this client. After it is called, no other methods shall be called on this client any
   * more. Exceptions will be thrown for calls that have no response yet.
   */
  void shutdown();
}
