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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.DELETE;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.WHERE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of delete operation.
 *
 * <pre>
 * {@literal
 * The "delete" object contains the following members:
 *
 *    "op":  "delete"               required
 *    "table": <table>              required
 *    "where": [<condition>*]       required
 *
 * The corresponding result object contains the following member:
 *
 *    "count": <integer>
 *
 * The operation deletes all the rows from "table" that match all the
 * conditions specified in "where".  The "count" member of the result
 * specifies the number of deleted rows.
 * }
 * </pre>
 */
public class Delete extends Operation {

  private final String table;

  @JsonProperty(value = WHERE)
  private final List<Condition> conditions;

  public Delete(String table) {
    this(table, new ArrayList<>());
  }

  /**
   * Create a {@link Delete} object.
   *
   * @param table value of the "table" field
   * @param conditions value of the "where" field
   */
  public Delete(String table, List<Condition> conditions) {
    super(DELETE);
    this.table = table;
    this.conditions = conditions;
  }

  public Delete where(String column, Function function, Value value) {
    conditions.add(new Condition(column, function, value));
    return this;
  }

  public Delete where(String column, Function function, String string) {
    return where(column, function, Atom.string(string));
  }

  public Delete where(String column, Function function, long integer) {
    return where(column, function, Atom.integer(integer));
  }

  public Delete where(String column, Function function, boolean bool) {
    return where(column, function, Atom.bool(bool));
  }

  public Delete where(String column, Function function, Uuid uuid) {
    return where(column, function, Atom.uuid(uuid));
  }

  public Delete where(String column, Function function, NamedUuid namedUuid) {
    return where(column, function, Atom.namedUuid(namedUuid));
  }

  public <K, V> Delete where(
      String column, Function function, java.util.Map<K, V> map
  ) {
    return where(column, function, Map.of(map));
  }

  public <T> Delete where(
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

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Delete)) {
      return false;
    }
    Delete that = (Delete) other;
    return Objects.equals(table, that.getTable())
        && Objects.equals(conditions, that.getWhere());
  }

  @Override
  public int hashCode() {

    return Objects.hash(table, conditions);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "table=" + table
        + ", where=" + conditions
        + "]";
  }
}
