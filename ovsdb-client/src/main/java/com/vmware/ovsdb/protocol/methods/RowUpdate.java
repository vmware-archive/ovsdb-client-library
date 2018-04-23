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

package com.vmware.ovsdb.protocol.methods;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Row;

import java.util.Objects;

public class RowUpdate {

  private Row oldRow;

  @JsonProperty(value = "new")
  private Row newRow;

  public RowUpdate() {
  }

  public RowUpdate(Row oldRow, Row newRow) {
    this.oldRow = oldRow;
    this.newRow = newRow;
  }

  public Row getOld() {
    return oldRow;
  }

  public RowUpdate setOld(Row oldRow) {
    this.oldRow = oldRow;
    return this;
  }

  public Row getNew() {
    return newRow;
  }

  public RowUpdate setNew(Row newRow) {
    this.newRow = newRow;
    return this;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof RowUpdate)) {
      return false;
    }
    RowUpdate rowUpdate = (RowUpdate) other;
    return Objects.equals(oldRow, rowUpdate.getOld())
        && Objects.equals(newRow, rowUpdate.getNew());
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldRow, newRow);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "old=" + oldRow
        + ", new=" + newRow
        + "]";
  }
}
