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

package com.vmware.ovsdb.protocol.operation.notation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vmware.ovsdb.protocol.operation.notation.deserializer.RowDeserializer;
import com.vmware.ovsdb.protocol.operation.notation.serializer.RowSerializer;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Representation of {@literal <row>}.
 *
 * <pre>
 * {@literal
 * <row>
 *   A JSON object that describes a table row or a subset of a table
 *   row.  Each member is the name of a table column paired with the
 *   <value> of that column.
 * }
 * </pre>
 */
@JsonSerialize(using = RowSerializer.class)
@JsonDeserialize(using = RowDeserializer.class)
public class Row {

  private java.util.Map<String, Value> columns;

  public Row(java.util.Map<String, Value> columns) {
    this.columns = columns;
  }

  public Row() {
    this(new HashMap<>());
  }

  public java.util.Map<String, Value> getColumns() {
    return columns;
  }

  private Row column(String name, Value value) {
    columns.put(name, value);
    return this;
  }

  public Row stringColumn(String name, String string) {
    return column(name, Atom.string(string));
  }

  public Row integerColumn(String name, Long integer) {
    return column(name, Atom.integer(integer));
  }

  public Row boolColumn(String name, Boolean bool) {
    return column(name, Atom.bool(bool));
  }

  public Row uuidColumn(String name, Uuid uuid) {
    return column(name, Atom.uuid(uuid));
  }

  public Row namedUuidColumn(String name, String uuidName) {
    return column(name, Atom.namedUuid(uuidName));
  }

  public <K, V> Row mapColumn(String name, java.util.Map<K, V> map) {
    return column(name, Map.of(map));
  }

  public <T> Row setColumn(String name, java.util.Set<T> set) {
    return column(name, Set.of(set));
  }

  private <T> T getAtomColumn(String name) {
    Atom<T> value = (Atom<T>) columns.get(name);
    return value == null ? null : value.getValue();
  }

  /**
   * Get the value from a column whose type is {@literal <string>}.
   *
   * @param name column name
   * @return the value from the column
   */
  public String getStringColumn(String name) {
    return getAtomColumn(name);
  }

  /**
   * Get the value from a column whose type is {@literal <integer>}.
   *
   * @param name column name
   * @return the value from the column
   */
  public Long getIntegerColumn(String name) {
    return getAtomColumn(name);
  }

  /**
   * Get the value from a column whose type is {@literal <bool>}.
   *
   * @param name column name
   * @return the value from the column
   */
  public Boolean getBooleanColumn(String name) {
    return getAtomColumn(name);
  }

  /**
   * Get the value from a column whose type is {@literal <uuid>}.
   *
   * @param name column name
   * @return the value from the column
   */
  public Uuid getUuidColumn(String name) {
    return getAtomColumn(name);
  }

  /**
   * Get the value from a column whose type is {@literal <map>}.
   *
   * @param <K> the type of the keys in the map
   * @param <V> the type of the values in the map
   * @param name column name
   * @return the value of the column
   */
  public <K, V> java.util.Map<K, V> getMapColumn(String name) {
    Map<K, V> value = ((Map<K, V>) columns.get(name));
    return value == null ? null : value.getPairs().stream().collect(
        Collectors.toMap(
            pair -> pair.getKey().getValue(),
            pair -> pair.getValue().getValue()
        ));
  }

  /**
   * Get the value from a column whose type is {@literal <set>}.
   *
   * @param <T> the type of the values in the set
   * @param name column name
   * @return the value of the column
   */
  public <T> java.util.Set<T> getSetColumn(String name) {
    Value value = columns.get(name);
    if (value == null) {
      return null;
    }
    // The set can be an <atom>
    Set set = value instanceof Atom ? Set.of((Atom) value) : (Set) value;
    return set.getSet().stream().map(Atom<T>::getValue)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Row)) {
      return false;
    }
    Row that = (Row) other;
    return Objects.equals(columns, that.getColumns());
  }

  @Override
  public int hashCode() {
    return Objects.hash(columns);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "columns=" + columns
        + "]";
  }
}
