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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.SELECT;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.WHERE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Representation of select operation.
 *
 * <pre>
 * {@literal
 * The "select" object contains the following members:
 *
 *    "op": "select"                required
 *    "table": <table>              required
 *    "where": [<condition>*]       required
 *    "columns": [<column>*]        optional
 *
 * The corresponding result object contains the following member:
 *
 *    "rows": [<row>*]
 *
 * The operation searches "table" for rows that match all the conditions
 * specified in "where".  If "where" is an empty array, every row in
 * "table" is selected.
 *
 * The "rows" member of the result is an array of objects.  Each object
 * corresponds to a matching row, with each column specified in
 * "columns" as a member, the column's name as the member name, and its
 * value as the member value.  If "columns" is not specified, all the
 * table's columns are included (including the internally generated
 * "_uuid" and "_version" columns).  If two rows of the result have the
 * same values for all included columns, only one copy of that row is
 * included in "rows".  Specifying "_uuid" within "columns" will avoid
 * dropping duplicates, since every row has a unique UUID.
 *
 * The ordering of rows within "rows" is unspecified.
 * }
 * </pre>
 */
@JsonPropertyOrder( {"op", "table", "where", "columns"})
public class Select extends Operation {

  private final String table;

  @JsonProperty(value = WHERE)
  private List<Condition> conditions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> columns;

  public Select(String table) {
    this(table, new ArrayList<>(), null);
  }

  public Select(String table, List<Condition> conditions) {
    this(table, conditions, null);
  }

  /**
   * Create an {@link Select} object.
   *
   * @param table value of the "table" field
   * @param conditions value of the "where" field
   * @param columns value of the "columns" field
   */
  @JsonCreator
  public Select(
      @JsonProperty(value = "table", required = true) String table,
      @JsonProperty(value = "conditions", required = true) List<Condition> conditions,
      @JsonProperty(value = "columns") List<String> columns
  ) {
    super(SELECT);
    this.table = table;
    this.conditions = conditions;
    this.columns = columns;
  }

  public Select where(String column, Function function, Value value) {
    conditions.add(new Condition(column, function, value));
    return this;
  }

  public Select where(String column, Function function, String string) {
    return where(column, function, Atom.string(string));
  }

  public Select where(String column, Function function, long integer) {
    return where(column, function, Atom.integer(integer));
  }

  public Select where(String column, Function function, boolean bool) {
    return where(column, function, Atom.bool(bool));
  }

  public Select where(String column, Function function, Uuid uuid) {
    return where(column, function, Atom.uuid(uuid));
  }

  public Select where(String column, Function function, NamedUuid namedUuid) {
    return where(column, function, Atom.namedUuid(namedUuid));
  }

  public <K, V> Select where(
      String column, Function function, java.util.Map<K, V> map
  ) {
    return where(column, function, Map.of(map));
  }

  public <T> Select where(
      String column, Function function, java.util.Set<T> set
  ) {
    return where(column, function, Set.of(set));
  }

  public Select columns(String... column) {
    columns = Arrays.asList(column);
    return this;
  }

  public String getTable() {
    return table;
  }

  public List<Condition> getWhere() {
    return conditions;
  }

  public List<String> getColumns() {
    return columns;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Select)) {
      return false;
    }
    Select that = (Select) other;
    return Objects.equals(table, that.getTable())
        && Objects.equals(conditions, that.getWhere())
        && Objects.equals(columns, that.getColumns());
  }

  @Override
  public int hashCode() {
    return Objects.hash(table, conditions, columns);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "table=" + table
        + ", where=" + conditions
        + ", columns=" + columns
        + "]";
  }
}
