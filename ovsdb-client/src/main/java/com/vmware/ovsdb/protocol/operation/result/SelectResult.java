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

package com.vmware.ovsdb.protocol.operation.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Row;

import java.util.List;
import java.util.Objects;

/**
 * Result of the "select" operation, which only contains one "row" field.
 */
public class SelectResult extends OperationResult {

  private List<Row> rows;

  @JsonCreator
  public SelectResult(@JsonProperty(value = "rows", required = true) List<Row> rows) {
    this.rows = rows;
  }

  public List<Row> getRows() {
    return rows;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SelectResult)) {
      return false;
    }
    SelectResult that = (SelectResult) other;
    return Objects.equals(rows, that.getRows());
  }

  @Override
  public int hashCode() {
    return Objects.hash(rows);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "rows=" + rows
        + "]";
  }
}
