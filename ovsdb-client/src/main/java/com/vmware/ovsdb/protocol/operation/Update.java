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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.UPDATE;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.WHERE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of update operation.
 *
 * <pre>
 * {@literal
 * The "update" object contains the following members:
 *
 *    "op": "update"                required
 *    "table": <table>              required
 *    "where": [<condition>*]       required
 *    "row": <row>                  required
 *
 * The corresponding result object contains the following member:
 *
 *    "count": <integer>
 *
 * The operation updates rows in a table.  It searches "table" for rows
 * that match all the conditions specified in "where".  For each
 * matching row, it changes the value of each column specified in "row"
 * to the value for that column specified in "row".  The "_uuid" and
 * "_version" columns of a table may not be directly updated with this
 * operation.  Columns designated read-only in the schema also may not
 * be updated.
 *
 * The "count" member of the result specifies the number of rows that
 * matched.
 *
 * The error that may be returned is:
 *
 *    "error": "constraint violation"
 *    One of the values in "row" does not satisfy the immediate
 *    constraints for its column's <base-type>.
 * }
 * </pre>
 */
public class Update extends Operation {

  private final String table;

  @JsonProperty(value = WHERE)
  private List<Condition> conditions;

  private Row row;

  /**
   * Create an {@link Update} object.
   *
   * @param table value of the "table" field
   * @param conditions value of the "where" field
   * @param row value of the "row" field
   */
  public Update(String table, List<Condition> conditions, Row row) {
    super(UPDATE);
    this.table = table;
    this.conditions = conditions;
    this.row = row;
  }

  public Update(String table, Row row) {
    this(table, new ArrayList<>(), row);
  }

  public Update where(String column, Function function, Value value) {
    conditions.add(new Condition(column, function, value));
    return this;
  }

  public Update where(String column, Function function, String string) {
    return where(column, function, Atom.string(string));
  }

  public Update where(String column, Function function, long integer) {
    return where(column, function, Atom.integer(integer));
  }

  public Update where(String column, Function function, boolean bool) {
    return where(column, function, Atom.bool(bool));
  }

  public Update where(String column, Function function, Uuid uuid) {
    return where(column, function, Atom.uuid(uuid));
  }

  public Update where(String column, Function function, NamedUuid namedUuid) {
    return where(column, function, Atom.namedUuid(namedUuid));
  }

  public <K, V> Update where(
      String column, Function function, java.util.Map<K, V> map
  ) {
    return where(column, function, Map.of(map));
  }

  public <T> Update where(
      String column, Function function, java.util.Set<T> set
  ) {
    return where(column, function, Set.of(set));
  }

  public String getTable() {
    return table;
  }

  public List<Condition> getWhere() {
    return conditions;
  }

  public Row getRow() {
    return row;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Update)) {
      return false;
    }
    Update that = (Update) other;
    return Objects.equals(table, that.getTable())
        && Objects.equals(conditions, that.getWhere())
        && Objects.equals(row, that.getRow());
  }

  @Override
  public int hashCode() {
    return Objects.hash(table, conditions, row);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "table=" + table
        + ", where=" + conditions
        + ", row=" + row
        + "]";
  }
}
