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

/**
 * Interface for a JSON-RPC 1.0 server that handles RPC requests from the clients.
 */
public interface JsonRpcV1Server {

  /**
   * Handle the request from the client.
   *
   * @param requestNode the request {@link JsonNode}
   * @throws JsonRpcException if fail to handle the request
   */
  void handleRequest(JsonNode requestNode) throws JsonRpcException;

  /**
   * Shutdown this server. After it is called, no other methods shall be called on this server any
   * more. The request that is being handled may be affected.
   */
  void shutdown();

}
