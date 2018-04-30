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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.WAIT;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Row;

import java.util.List;
import java.util.Objects;

/**
 * Representation of wait operation.
 *
 * <pre>
 * {@literal
 * The "wait" object contains the following members:
 *
 *    "op": "wait"                        required
 *    "timeout": <integer>                optional
 *    "table": <table>                    required
 *    "where": [<condition>*]             required
 *    "columns": [<column>*]              required
 *    "until": "==" or "!="               required
 *    "rows": [<row>*]                    required
 *
 * There is no corresponding result object.
 *
 * The operation waits until a condition becomes true.
 *
 * If "until" is "==", it checks whether the query on "table" specified
 * by "where" and "columns", which is evaluated in the same way as
 * specified for "select", returns the result set specified by "rows".
 * If it does, then the operation completes successfully.  Otherwise,
 * the entire transaction rolls back.  It is automatically restarted
 * later, after a change in the database makes it possible for the
 * operation to succeed.  The client will not receive a response until
 * the operation permanently succeeds or fails.
 *
 * If "until" is "!=", the sense of the test is negated.  That is, as
 * long as the query on "table" specified by "where" and "columns"
 * returns "rows", the transaction will be rolled back and restarted
 * later.
 *
 * If "timeout" is specified, then the transaction aborts after the
 * specified number of milliseconds.  The transaction is guaranteed to
 * be attempted at least once before it aborts.  A "timeout" of 0 will
 * abort the transaction on the first mismatch.
 *
 * The error that may be returned is:
 *
 * "error":  "timed out"
 *   The "timeout" was reached before the transaction was able to
 *   complete.
 * }
 * </pre>
 */
public class Wait extends Operation {

  public enum Until {
    EQUAL("=="),
    NOTEQUAL("!=");

    private String name;

    Until(String name) {
      this.name = name;
    }

    @JsonValue
    public String toString() {
      return name;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Integer timeout;

  private final String table;

  private final List<Condition> where;

  private final List<String> columns;

  private final Until until;

  private final List<Row> rows;

  public Wait(
      String table, List<Condition> where,
      List<String> columns, Until until, List<Row> rows
  ) {
    this(table, null, where, columns, until, rows);
  }

  /**
   * Create a {@link Wait} object.
   *
   * @param table value of the "table" field
   * @param timeout value of the "timeout" field
   * @param where value of the "where" field
   * @param columns value of the "columns" field
   * @param until value of the "until" field
   * @param rows value of the "rows" field
   */
  public Wait(
      String table, Integer timeout, List<Condition> where,
      List<String> columns, Until until, List<Row> rows
  ) {
    super(WAIT);
    this.table = table;
    this.timeout = timeout;
    this.where = where;
    this.columns = columns;
    this.until = until;
    this.rows = rows;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public String getTable() {
    return table;
  }

  public List<Condition> getWhere() {
    return where;
  }

  public List<String> getColumns() {
    return columns;
  }

  public Until getUntil() {
    return until;
  }

  public List<Row> getRows() {
    return rows;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Wait)) {
      return false;
    }
    Wait that = (Wait) other;
    return Objects.equals(timeout, that.getTimeout())
        && Objects.equals(table, that.getTable())
        && Objects.equals(where, that.getWhere())
        && Objects.equals(columns, that.getColumns())
        && until == that.getUntil()
        && Objects.equals(rows, that.getRows());
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeout, table, where, columns, until, rows);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "timeout=" + timeout
        + ", table=" + table
        + ", where=" + where
        + ", columns=" + columns
        + ", until=" + until
        + ", rows=" + rows
        + "]";
  }
}
