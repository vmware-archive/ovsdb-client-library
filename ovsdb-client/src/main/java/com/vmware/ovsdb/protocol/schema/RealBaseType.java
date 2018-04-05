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
 * Represent a {@literal <base-type>} with a real {@literal <atomic-type>} as it's type.
 *
 * @see BaseType
 * @see AtomicType
 */
public class RealBaseType extends BaseType {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Double minReal;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Double maxReal;

  /**
   * Create a {@link RealBaseType} object.
   */
  public RealBaseType() {
    this(null, null);
  }

  /**
   * Create a {@link RealBaseType} object.
   *
   * @param enums value of the "enums" field
   */
  public RealBaseType(Value enums) {
    super(AtomicType.REAL, enums);
    this.minReal = null;
    this.maxReal = null;
  }

  /**
   * Create a {@link RealBaseType} object.
   *
   * @param minReal value of the "minReal" field
   * @param maxReal value of the "maxReal" field
   */
  public RealBaseType(Double minReal, Double maxReal) {
    super(AtomicType.REAL);
    this.minReal = minReal;
    this.maxReal = maxReal;
  }

  public Double getMinReal() {
    return minReal;
  }

  public Double getMaxReal() {
    return maxReal;
  }

  @Override
  public int hashCode() {
    int result = minReal != null
        ? minReal.hashCode()
        : 0;
    result = 31 * result + (maxReal != null
        ? maxReal.hashCode()
        : 0);
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof RealBaseType)) {
      return false;
    }

    RealBaseType that = (RealBaseType) other;

    if (minReal != null
        ? !minReal.equals(that.minReal)
        : that.minReal != null) {
      return false;
    }
    return maxReal != null
        ? maxReal.equals(that.maxReal)
        : that.maxReal == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "minReal=" + minReal
        + ", maxReal=" + maxReal
        + "]";
  }
}
