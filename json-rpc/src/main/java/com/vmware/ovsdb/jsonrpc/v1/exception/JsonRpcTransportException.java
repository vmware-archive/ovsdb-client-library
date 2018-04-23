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

package com.vmware.ovsdb.jsonrpc.v1.exception;

/**
 * Thrown to indicate that there is something wrong with the JSON-RPC transporter.
 */
public class JsonRpcTransportException extends JsonRpcException {

  public JsonRpcTransportException(String message) {
    super(message);
  }

  public JsonRpcTransportException(Throwable cause) {
    super(cause);
  }

}
