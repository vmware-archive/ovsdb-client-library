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
 * Thrown to indicate that there is something wrong with this JSON-RPC client / server.
 */
public class JsonRpcException extends Exception {

  public JsonRpcException() {
    super();
  }

  public JsonRpcException(String message) {
    super(message);
  }

  public JsonRpcException(Throwable cause) {
    super(cause);
  }

  public JsonRpcException(String message, Throwable cause) {
    super(message, cause);
  }
}
