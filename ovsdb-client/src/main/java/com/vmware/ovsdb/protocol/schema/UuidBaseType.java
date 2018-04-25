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
 * Represent a {@literal <base-type>} with a uuid {@literal <atomic-type>} as it's type.
 *
 * @see BaseType
 * @see AtomicType
 */
public class UuidBaseType extends BaseType {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final String refTable;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final String refType;

  /**
   * Create a {@link UuidBaseType} object.
   */
  public UuidBaseType() {
    this(null, null);
  }

  /**
   * Create a {@link UuidBaseType} object.
   *
   * @param enums value of "enum" field
   */
  public UuidBaseType(Value enums) {
    super(AtomicType.UUID, enums);
    this.refTable = null;
    this.refType = null;
  }

  /**
   * Create a {@link UuidBaseType} object.
   *
   * @param refTable value of "refTable" field
   * @param refType value of "refType" field
   */
  public UuidBaseType(String refTable, String refType) {
    super(AtomicType.UUID);
    this.refTable = refTable;
    this.refType = refType;
  }

  public String getRefTable() {
    return refTable;
  }

  public String getRefType() {
    return refType;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof UuidBaseType)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    UuidBaseType that = (UuidBaseType) other;
    return Objects.equals(refTable, that.getRefTable())
        && Objects.equals(refType, that.getRefType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), refTable, refType);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "refTable=" + refTable
        + ", refType=" + refType
        + "]";
  }
}
