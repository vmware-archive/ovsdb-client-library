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

import static com.vmware.ovsdb.protocol.util.OvsdbConstant.MUTATE;
import static com.vmware.ovsdb.protocol.util.OvsdbConstant.WHERE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Atom;
import com.vmware.ovsdb.protocol.operation.notation.Condition;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Map;
import com.vmware.ovsdb.protocol.operation.notation.Mutation;
import com.vmware.ovsdb.protocol.operation.notation.Mutator;
import com.vmware.ovsdb.protocol.operation.notation.NamedUuid;
import com.vmware.ovsdb.protocol.operation.notation.Set;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of mutate operation.
 *
 * <pre>
 * {@literal
 * The "mutate" object contains the following members:
 *
 *    "op":  "mutate"               required
 *    "table": <table>              required
 *    "where": [<condition>*]       required
 *    "mutations": [<mutation>*]    required
 *
 * The corresponding result object contains the following member:
 *
 *    "count": <integer>
 *
 * The operation mutates rows in a table.  It searches "table" for rows
 * that match all the conditions specified in "where".  For each
 * matching row, it mutates its columns as specified by each <mutation>
 * in "mutations", in the order specified.
 *
 * The "_uuid" and "_version" columns of a table may not be directly
 * modified with this operation.  Columns designated read-only in the
 * schema also may not be updated.
 *
 * The "count" member of the result specifies the number of rows that
 * matched.
 *
 * The errors that may be returned are:
 *
 * "error":  "domain error"
 *    The result of the mutation is not mathematically defined, e.g.,
 *    division by zero.
 *
 * "error":  "range error"
 *    The result of the mutation is not representable within the
 *    database's format, e.g., an integer result outside the range
 *    INT64_MIN...INT64_MAX or a real result outside the range
 *    -DBL_MAX...DBL_MAX.
 *
 * "error": "constraint violation"
 *    The mutation caused the column's value to violate a constraint,
 *    e.g., it caused a column to have more or fewer values than are
 *    allowed, an arithmetic operation caused a set or map to have
 *    duplicate elements, or it violated a constraint specified by a
 *    column's <base-type>.
 * }
 * </pre>
 */
public class Mutate extends Operation {

  private final String table;

  @JsonProperty(value = WHERE)
  private List<Condition> conditions;

  private List<Mutation> mutations;

  public Mutate(String table) {
    this(table, new ArrayList<>(), new ArrayList<>());
  }

  /**
   * Create a {@link Mutate} object.
   *
   * @param table value of the "table" field
   * @param conditions value of the "where" field
   * @param mutations value of the "mutations" field
   */
  public Mutate(
      String table, List<Condition> conditions, List<Mutation> mutations
  ) {
    super(MUTATE);
    this.table = table;
    this.conditions = conditions;
    this.mutations = mutations;
  }

  public Mutate where(String column, Function function, Value value) {
    conditions.add(new Condition(column, function, value));
    return this;
  }

  public Mutate where(String column, Function function, String string) {
    return where(column, function, Atom.string(string));
  }

  public Mutate where(String column, Function function, long integer) {
    return where(column, function, Atom.integer(integer));
  }

  public Mutate where(String column, Function function, boolean bool) {
    return where(column, function, Atom.bool(bool));
  }

  public Mutate where(String column, Function function, Uuid uuid) {
    return where(column, function, Atom.uuid(uuid));
  }

  public Mutate where(String column, Function function, NamedUuid namedUuid) {
    return where(column, function, Atom.namedUuid(namedUuid));
  }

  public <K, V> Mutate where(
      String column, Function function, java.util.Map<K, V> map
  ) {
    return where(column, function, Map.of(map));
  }

  public <T> Mutate where(
      String column, Function function, java.util.Set<T> set
  ) {
    return where(column, function, Set.of(set));
  }

  private Mutate mutation(String column, Mutator mutator, Value value) {
    this.mutations.add(new Mutation(column, mutator, value));
    return this;
  }

  public Mutate mutation(String column, Mutator mutator, String value) {
    return mutation(column, mutator, Atom.string(value));
  }

  public Mutate mutation(String column, Mutator mutator, long value) {
    return mutation(column, mutator, Atom.integer(value));
  }

  public Mutate mutation(String column, Mutator mutator, boolean value) {
    return mutation(column, mutator, Atom.bool(value));
  }

  public Mutate mutation(String column, Mutator mutator, Uuid value) {
    return mutation(column, mutator, Atom.uuid(value));
  }

  public Mutate mutation(String column, Mutator mutator, NamedUuid value) {
    return mutation(column, mutator, Atom.namedUuid(value));
  }

  public <K, V> Mutate mutation(
      String column, Mutator mutator, java.util.Map<K, V> map
  ) {
    return mutation(column, mutator, Map.of(map));
  }

  public <T> Mutate mutation(
      String column, Mutator mutator, java.util.Set<T> set
  ) {
    return mutation(column, mutator, Set.of(set));
  }

  public String getTable() {
    return table;
  }

  public List<Condition> getWhere() {
    return conditions;
  }

  public List<Mutation> getMutations() {
    return mutations;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Mutate)) {
      return false;
    }
    Mutate that = (Mutate) other;
    return Objects.equals(table, that.getTable())
        && Objects.equals(conditions, that.getWhere())
        && Objects.equals(mutations, that.getMutations());
  }

  @Override
  public int hashCode() {

    return Objects.hash(table, conditions, mutations);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "table=" + table
        + ", where=" + conditions
        + ", mutations=" + mutations
        + "]";
  }
}
