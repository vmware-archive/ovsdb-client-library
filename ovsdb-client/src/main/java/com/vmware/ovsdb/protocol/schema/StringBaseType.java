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
 * Represent a {@literal <base-type>} with a string {@literal <atomic-type>} as it's type.
 *
 * @see BaseType
 * @see AtomicType
 */
public class StringBaseType extends BaseType {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long minLength;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long maxLength;

  /**
   * Create a {@link StringBaseType} object.
   */
  public StringBaseType() {
    this(null, null);
  }

  /**
   * Create a {@link StringBaseType} object.
   *
   * @param enums value of the "enums" field
   */
  public StringBaseType(Value enums) {
    super(AtomicType.STRING, enums);
    this.minLength = null;
    this.maxLength = null;
  }

  /**
   * Create a {@link StringBaseType} object.
   *
   * @param minLength value of the "minLength" field
   * @param maxLength value of the "maxLength" field
   */
  public StringBaseType(Long minLength, Long maxLength) {
    super(AtomicType.STRING);
    this.minLength = minLength;
    this.maxLength = maxLength;
  }

  public Long getMinLength() {
    return minLength;
  }

  public Long getMaxLength() {
    return maxLength;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof StringBaseType)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    StringBaseType that = (StringBaseType) other;
    return Objects.equals(minLength, that.getMinLength())
        && Objects.equals(maxLength, that.getMaxLength());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), minLength, maxLength);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "minLength=" + minLength
        + ", maxLength=" + maxLength
        + "]";
  }
}
