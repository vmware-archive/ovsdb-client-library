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

package com.vmware.ovsdb.protocol.operation;

/**
 * The operations that may be performed as part of a "transact" RPC request (see Section 4.1.3) are
 * described in the following subsections.  Each of these operations is a JSON object that may be
 * included as one of the elements of the "params" array that is one of the elements of the
 * "transact" request.  The details of each object, its semantics, results, and possible errors are
 * described below.
 *
 * @see Insert
 * @see Select
 * @see Update
 * @see Mutate
 * @see Delete
 * @see Wait
 * @see Commit
 * @see Abort
 * @see Comment
 * @see Assert
 */
public abstract class Operation {

  private final String op;

  protected Operation(String op) {
    this.op = op;
  }

  public String getOp() {
    return op;
  }
}
