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

package com.vmware.ovsdb.protocol.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Representation of {@literal <table-schema>}.
 *
 * <pre>
 * {@literal
 * <table-schema>
 *   A JSON object with the following members:
 *
 *       "columns": {<id>: <column-schema>, ...}   required
 *       "maxRows": <integer>                      optional
 *       "isRoot": <boolean>                       optional
 *       "indexes": [<column-set>*]                optional
 *
 *   The value of "columns" is a JSON object whose names are column
 *   names and whose values are <column-schema>s.
 *
 *   Every table has the following columns whose definitions are not
 *   included in the schema:
 *
 *       "_uuid": This column, which contains exactly one UUID value, is
 *       initialized to a random value by the database engine when it
 *       creates a row.  It is read-only, and its value never changes
 *       during the lifetime of a row.
 *
 *       "_version": Like "_uuid", this column contains exactly one UUID
 *       value, initialized to a random value by the database engine
 *       when it creates a row, and it is read-only.  However, its value
 *       changes to a new random value whenever any other field in the
 *       row changes.  Furthermore, its value is ephemeral: when the
 *       database is closed and reopened, or when the database process
 *       is stopped and then started again, each "_version" also changes
 *       to a new random value.
 *
 *   If "maxRows" is specified, as a positive integer, it limits the
 *   maximum number of rows that may be present in the table.  This is
 *   a "deferred" constraint, enforced only at transaction commit time
 *   (see the "transact" request in Section 4.1.3).  If "maxRows" is
 *   not specified, the size of the table is limited only by the
 *   resources available to the database server. "maxRows" constraints
 *   are enforced after unreferenced rows are deleted from tables with
 *   a false "isRoot".
 *
 *   The "isRoot" boolean is used to determine whether rows in the
 *   table require strong references from other rows to avoid garbage
 *   collection.  (See the discussion of "strong" and "weak" references
 *   below in the description of <base-type>.)  If "isRoot" is
 *   specified as true, then rows in the table exist independent of any
 *   references (they can be thought of as part of the "root set" in a
 *   garbage collector).  If "isRoot" is omitted or specified as false,
 *   then any given row in the table may exist only when there is at
 *   least one reference to it, with refType "strong", from a different
 *   row (in the same table or a different table).  This is a
 *   "deferred" action: unreferenced rows in the table are deleted just
 *   before transaction commit.
 *
 *   For compatibility with schemas created before "isRoot" was
 *   introduced, if "isRoot" is omitted or false in every
 *   <table-schema> in a given <database-schema>, then every table is
 *   part of the root set.
 *
 *   If "indexes" is specified, it must be an array of zero or more
 *   <column-set>s.  A <column-set> is an array of one or more strings,
 *   each of which names a column.  Each <column-set> is a set of
 *   columns whose values, taken together within any given row, must be
 *   unique within the table.  This is a "deferred" constraint,
 *   enforced only at transaction commit time, after unreferenced rows
 *   are deleted and dangling weak references are removed.  Ephemeral
 *   columns may not be part of indexes.
 * }
 * </pre>
 */
public class TableSchema {

  private final Map<String, ColumnSchema> columns;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long maxRows;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Boolean isRoot;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final List<Set<String>> indexes;

  /**
   * Create a {@link TableSchema} object.
   *
   * @param columns value of the "columns" filed
   * @param maxRows value of the "maxRows" field
   * @param isRoot value of the "isRoot" field
   * @param indexes value of the "indexes" field
   */
  @JsonCreator
  public TableSchema(
      @JsonProperty(value = "columns", required = true) Map<String,
          ColumnSchema> columns,
      @JsonProperty(value = "maxRows") Long maxRows,
      @JsonProperty(value = "isRoot") Boolean isRoot,
      @JsonProperty(value = "indexes") List<Set<String>> indexes
  ) {
    this.columns = columns;
    this.maxRows = maxRows;
    this.isRoot = isRoot;
    this.indexes = indexes;
  }

  public Map<String, ColumnSchema> getColumns() {
    return columns;
  }

  public Long getMaxRows() {
    return maxRows;
  }

  public Boolean getIsRoot() {
    return isRoot;
  }

  public List<Set<String>> getIndexes() {
    return indexes;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TableSchema)) {
      return false;
    }
    TableSchema that = (TableSchema) other;
    return Objects.equals(columns, that.getColumns())
        && Objects.equals(maxRows, that.getMaxRows())
        && Objects.equals(isRoot, that.getIsRoot())
        && Objects.equals(indexes, that.getIndexes());
  }

  @Override
  public int hashCode() {

    return Objects.hash(columns, maxRows, isRoot, indexes);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "columns=" + columns
        + ", maxRows=" + maxRows
        + ", isRoot=" + isRoot
        + ", indexes=" + indexes
        + "]";
  }
}
