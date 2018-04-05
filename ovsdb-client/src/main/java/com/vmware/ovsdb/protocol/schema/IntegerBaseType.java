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
   * @param enums value of the "enums" field
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
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (minInteger != null
        ? minInteger.hashCode()
        : 0);
    result = 31 * result + (maxInteger != null
        ? maxInteger.hashCode()
        : 0);
    return result;
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

    if (minInteger != null
        ? !minInteger.equals(that.minInteger)
        : that.minInteger != null) {
      return false;
    }
    return maxInteger != null
        ? maxInteger.equals(that.maxInteger)
        : that.maxInteger == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "minInteger=" + minInteger
        + ", maxInteger=" + maxInteger
        + "]";
  }
}
