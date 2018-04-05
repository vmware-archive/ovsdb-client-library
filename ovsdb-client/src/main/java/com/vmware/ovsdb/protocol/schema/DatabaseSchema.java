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

import java.util.Map;

/**
 * Representation of {@literal <database-schema>}.
 *
 * <pre>
 * {@literal
 * <database-schema>
 *   A JSON object with the following members:
 *
 *       "name": <id>                            required
 *       "version": <version>                    required
 *       "cksum": <string>                       optional
 *       "tables": {<id>: <table-schema>, ...}   required
 *
 *   The "name" identifies the database as a whole.  It must be
 *   provided to most JSON-RPC requests to identify the database being
 *   operated on.
 *
 *   The "version" reports the version of the database schema.  It is
 *   REQUIRED to be present.  Open vSwitch semantics for "version" are
 *   described in [DB-SCHEMA].  Other schemas may use it differently.
 *
 *   The "cksum" optionally reports an implementation-defined checksum
 *   for the database schema.  Its use is primarily as a tool for
 *   schema developers, and clients SHOULD ignore it.
 *
 *   The value of "tables" is a JSON object whose names are table names
 *   and whose values are <table-schema>s.
 * }
 * </pre>
 */
public class DatabaseSchema {

  private final String name;

  private final String version;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final String cksum;

  private final Map<String, TableSchema> tables;

  /**
   * Create a {@link DatabaseSchema} object.
   *
   * @param name value of the "name" field
   * @param version value of the "version" field
   * @param cksum value of the "cksum" field
   * @param tables value of the "tables" field
   */
  @JsonCreator
  public DatabaseSchema(
      @JsonProperty(value = "name", required = true) String name,
      @JsonProperty(value = "version", required = true) String version,
      @JsonProperty(value = "cksum") String cksum,
      @JsonProperty(value = "tables", required = true) Map<String,
          TableSchema> tables
  ) {
    this.name = name;
    this.version = version;
    this.cksum = cksum;
    this.tables = tables;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getCksum() {
    return cksum;
  }

  public Map<String, TableSchema> getTables() {
    return tables;
  }

  @Override
  public int hashCode() {
    int result = name != null
        ? name.hashCode()
        : 0;
    result = 31 * result + (version != null
        ? version.hashCode()
        : 0);
    result = 31 * result + (cksum != null
        ? cksum.hashCode()
        : 0);
    result = 31 * result + (tables != null
        ? tables.hashCode()
        : 0);
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof DatabaseSchema)) {
      return false;
    }

    DatabaseSchema that = (DatabaseSchema) other;

    if (name != null
        ? !name.equals(that.name)
        : that.name != null) {
      return false;
    }
    if (version != null
        ? !version.equals(that.version)
        : that.version != null) {
      return false;
    }
    if (cksum != null
        ? !cksum.equals(that.cksum)
        : that.cksum != null) {
      return false;
    }
    return tables != null
        ? tables.equals(that.tables)
        : that.tables == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "name=" + name
        + ", version=" + version
        + ", cksum=" + cksum
        + ", tables=" + tables
        + "]";
  }
}
