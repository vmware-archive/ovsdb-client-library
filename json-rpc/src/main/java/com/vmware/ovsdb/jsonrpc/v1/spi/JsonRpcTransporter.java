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

package com.vmware.ovsdb.jsonrpc.v1.spi;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;

/**
 * The underlying transport com.vmware.ovsdb.protocol for JSON-RPC. The implementation must be
 * thread-safe.
 */
public interface JsonRpcTransporter {

  /**
   * Send a JSON data to peer.
   *
   * @param data JSON data to send
   * @throws JsonRpcTransportException if fail to send the data
   */
  void send(JsonNode data) throws JsonRpcTransportException;

  /**
   * Close the transporter.
   */
  void close();

}
