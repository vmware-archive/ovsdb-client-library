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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.INSERT;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Row;

import java.util.Objects;

/**
 * Representation ofo insert operation.
 *
 * <pre>
 * {@literal
 * The "insert" object contains the following members:
 *
 *    "op": "insert"          required
 *    "table": <table>        required
 *    "row": <row>            required
 *    "uuid-name": <id>       optional
 *
 * The corresponding result object contains the following member:
 *
 *    "uuid": <uuid>
 *
 * The operation inserts "row" into "table".  If "row" does not specify
 * values for all the columns in "table", those columns receive default
 * values.  The default value for a column depends on its type.  The
 * default for a column whose <type> specifies a "min" of 0 is an empty
 * set or empty map.  Otherwise, the default is a single value or a
 * single key-value pair, whose value(s) depend on its <atomic-type>:
 *
 * o  "integer" or "real": 0
 *
 * o  "boolean": false
 *
 * o  "string": "" (the empty string)
 *
 * o  "uuid": 00000000-0000-0000-0000-000000000000
 *
 * The new row receives a new, randomly generated UUID.  If "uuid-name"
 * is supplied, then it is an error if <id> is not unique among the
 * "uuid-name"s supplied on all the "insert" operations within this
 * transaction.  The UUID for the new row is returned as the "uuid"
 * member of the result.
 *
 * The errors that may be returned are as follows:
 *
 * "error": "duplicate uuid-name"
 *    The same "uuid-name" appears on another "insert" operation within
 *    this transaction.
 *
 * "error": "constraint violation"
 *    One of the values in "row" does not satisfy the immediate
 *    constraints for its column's <base-type>.  This error will occur
 *    for columns that are not explicitly set by "row" if the default
 *    value does not satisfy the column's constraints.
 * }
 * </pre>
 */
public class Insert extends Operation {

  private final String table;

  private final Row row;

  @JsonProperty(value = "uuid-name")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String uuidName;

  public Insert(String table, Row row) {
    this(table, row, null);
  }

  /**
   * Create a {@link Insert} object.
   *
   * @param table value of the "table" field
   * @param row value of the "row" field
   * @param uuidName value of the "uuidName" field
   */
  public Insert(String table, Row row, String uuidName) {
    super(INSERT);
    this.table = table;
    this.row = row;
    this.uuidName = uuidName;
  }

  public Insert withUuidName(String uuidName) {
    this.uuidName = uuidName;
    return this;
  }

  public String getTable() {
    return table;
  }

  public Row getRow() {
    return row;
  }

  public String getUuidName() {
    return uuidName;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Insert)) {
      return false;
    }
    Insert that = (Insert) other;
    return Objects.equals(table, that.getTable())
        && Objects.equals(row, that.getRow())
        && Objects.equals(uuidName, that.getUuidName());
  }

  @Override
  public int hashCode() {

    return Objects.hash(table, row, uuidName);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "table=" + table
        + ", row=" + row
        + ", uuidName=" + uuidName
        + "]";
  }
}
