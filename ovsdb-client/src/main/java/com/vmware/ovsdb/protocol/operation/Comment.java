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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.COMMENT;

/**
 * <pre>
 * {@literal
 * The "comment" object contains the following members:
 *
 *    "op": "comment"                    required
 *    "comment": <string>                required
 *
 * There is no corresponding result object.
 *
 * The operation provides information to a database administrator on the
 * purpose of a transaction.  The ovsdb-server implementation, for
 * example, adds comments in transactions that modify the database to
 * the database journal.  This can be helpful in debugging, e.g., when
 * there are multiple clients writing to a database.  An example of this
 * can be seen in the ovs-vsctl tool, a command line tool that interacts
 * with ovsdb-server.  When performing operations on the database, it
 * includes the command that was invoked (e.g., "ovs-vsctl add-br br0")
 * as a comment in the transaction, which can then be seen in the
 * journal alongside the changes that were made to the tables in the
 * database.
 * }
 * </pre>
 */
public class Comment extends Operation {

  private final String comment;

  // TODO: Consider using the validation framework
  public Comment(String comment) {
    super(COMMENT);
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "comment=" + comment
        + "]";
  }
}
