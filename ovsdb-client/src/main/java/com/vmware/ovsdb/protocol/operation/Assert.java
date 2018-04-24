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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.ASSERT;

import java.util.Objects;

/**
 * <pre>
 * {@literal
 * The assert object contains the following members:
 *
 *     "op": "assert"                     required
 *     "lock": <id>                       required
 *
 * Result object has no members.
 *
 * The assert operation causes the transaction to be aborted if the
 * client does not own the lock named <id>.
 *
 * The error that may be returned is:
 *
 * "error": "not owner"
 *     The client does not own the named lock.
 * }
 * </pre>
 */
public class Assert extends Operation {

  private final String lock;

  public Assert(String lock) {
    super(ASSERT);
    this.lock = lock;
  }

  public String getLock() {
    return lock;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Assert)) {
      return false;
    }
    Assert that = (Assert) other;
    return Objects.equals(lock, that.getLock());
  }

  @Override
  public int hashCode() {
    return Objects.hash(lock);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "lock=" + lock
        + "]";
  }
}
