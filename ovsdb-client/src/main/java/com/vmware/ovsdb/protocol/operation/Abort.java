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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.ABORT;

/**
 * <pre>
 * {@literal
 * The "abort" object contains the following member:
 *
 *    "op":  "abort"                      required
 *
 * There is no corresponding result object (the operation never
 * succeeds).
 *
 * The operation aborts the entire transaction with an error.  This may
 * be useful for testing.
 *
 * The error that will be returned is:
 *
 * "error": "aborted"
 *    This operation always fails with this error.
 * }
 * </pre>
 */
public class Abort extends Operation {

  protected Abort() {
    super(ABORT);
  }
}
