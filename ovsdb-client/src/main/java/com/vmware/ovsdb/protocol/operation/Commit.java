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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.COMMIT;

import java.util.Objects;

/**
 * <pre>
 * {@literal
 * The "commit" object contains the following members:
 *
 *    "op": "commit"                      required
 *    "durable": <boolean>                required
 *
 * There is no corresponding result object.
 *
 * If "durable" is specified as true, then the transaction, if it
 * commits, will be stored durably (to disk) before the reply is sent to
 * the client.  This operation with "durable" set to false is
 * effectively a no-op.
 *
 * The error that may be returned is:
 *
 * "error": "not supported"
 *    When "durable" is true, this database implementation does not
 *    support durable commits.
 * }
 * </pre>
 */
public class Commit extends Operation {

  private final boolean durable;

  public Commit(boolean durable) {
    super(COMMIT);
    this.durable = durable;
  }

  public boolean isDurable() {
    return durable;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Commit)) {
      return false;
    }
    Commit that = (Commit) other;
    return durable == that.isDurable();
  }

  @Override
  public int hashCode() {

    return Objects.hash(durable);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [" + "durable=" + durable + "]";
  }
}
