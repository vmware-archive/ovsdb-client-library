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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.ovsdb.protocol.operation.notation.Value;

import java.util.Objects;

/**
 * Represent a {@literal <base-type>} with a integer {@literal <atomic-type>} as it's type.
 *
 * @see BaseType
 * @see AtomicType
 */
public class IntegerBaseType extends BaseType {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long minInteger;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long maxInteger;

  /**
   * Create a {@link IntegerBaseType} object.
   */
  public IntegerBaseType() {
    this(null, null);
  }

  /**
   * Create a {@link IntegerBaseType} object.
   *
   * @param enums value of the "enum" field
   */
  public IntegerBaseType(Value enums) {
    super(AtomicType.INTEGER, enums);
    this.minInteger = null;
    this.maxInteger = null;
  }

  /**
   * Create a {@link IntegerBaseType} object.
   *
   * @param minInteger value of the "minInteger" field
   * @param maxInteger value of the "maxInteger" field
   */
  public IntegerBaseType(Long minInteger, Long maxInteger) {
    super(AtomicType.INTEGER);
    this.minInteger = minInteger;
    this.maxInteger = maxInteger;
  }

  public Long getMinInteger() {
    return minInteger;
  }

  public Long getMaxInteger() {
    return maxInteger;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof IntegerBaseType)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    IntegerBaseType that = (IntegerBaseType) other;
    return Objects.equals(minInteger, that.getMinInteger())
        && Objects.equals(maxInteger, that.getMaxInteger());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), minInteger, maxInteger);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "minInteger=" + minInteger
        + ", maxInteger=" + maxInteger
        + "]";
  }
}
