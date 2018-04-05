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

  public void setOld(Row oldRow) {
    this.oldRow = oldRow;
  }

  public Row getNew() {
    return newRow;
  }

  public void setNew(Row newRow) {
    this.newRow = newRow;
  }

  @Override
  public int hashCode() {
    int result = oldRow != null
        ? oldRow.hashCode()
        : 0;
    result = 31 * result + (newRow != null
        ? newRow.hashCode()
        : 0);
    return result;
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

    if (oldRow != null
        ? !oldRow.equals(rowUpdate.oldRow)
        : rowUpdate.oldRow != null) {
      return false;
    }
    return newRow != null
        ? newRow.equals(rowUpdate.newRow)
        : rowUpdate.newRow == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "old=" + oldRow
        + ", new=" + newRow
        + "]";
  }
}
