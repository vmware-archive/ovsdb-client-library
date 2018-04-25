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

import java.util.Objects;

/**
 * Representation of {@literal <column-schema>}.
 *
 * <pre>
 * {@literal
 * <column-schema>
 *   A JSON object with the following members:
 *
 *       "type": <type>                            required
 *       "ephemeral": <boolean>                    optional
 *       "mutable": <boolean>                      optional
 *
 *   The "type" specifies the type of data stored in this column.
 *
 *   If "ephemeral" is specified as true, then this column's values are
 *   not guaranteed to be durable; they may be lost when the database
 *   restarts.  A column whose type (either key or value) is a strong
 *   reference to a table that is not part of the root set is always
 *   durable, regardless of this value.  (Otherwise, restarting the
 *   database could lose entire rows.)
 *
 *   If "mutable" is specified as false, then this column's values may
 *   not be modified after they are initially set with the "insert"
 *   operation.
 * }
 * </pre>
 */
public class ColumnSchema {

  private final Type type;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Boolean ephemeral;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Boolean mutable;

  /**
   * Create a {@link ColumnSchema} object.
   */
  public ColumnSchema(Type type) {
    this(type, null, null);
  }

  /**
   * Create a {@link ColumnSchema} object.
   *
   * @param type value of the "type" field
   * @param ephemeral value of the "ephemeral" field
   * @param mutable value of the "mutable" field
   */
  @JsonCreator
  public ColumnSchema(
      @JsonProperty(value = "type", required = true) Type type,
      @JsonProperty(value = "ephemeral") Boolean ephemeral,
      @JsonProperty(value = "mutable") Boolean mutable
  ) {
    this.type = type;
    this.ephemeral = ephemeral;
    this.mutable = mutable;
  }

  public Type getType() {
    return type;
  }

  public Boolean getEphemeral() {
    return ephemeral;
  }

  public Boolean getMutable() {
    return mutable;
  }

  // TODO: Should we be aware of the default value?
  // For e.g., if one <column-schema> doest not have "ephemeral"
  // and the other has false as "ephemeral", are they equal?
  // Same for all other notations.
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ColumnSchema)) {
      return false;
    }
    ColumnSchema that = (ColumnSchema) other;
    return Objects.equals(type, that.getType())
        && Objects.equals(ephemeral, that.getEphemeral())
        && Objects.equals(mutable, that.getMutable());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, ephemeral, mutable);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "type=" + type
        + ", ephemeral=" + ephemeral
        + ", mutable=" + mutable
        + "]";
  }
}
