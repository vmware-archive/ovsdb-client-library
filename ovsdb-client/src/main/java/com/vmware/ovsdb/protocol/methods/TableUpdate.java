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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.methods.deserializer.TableUpdateDeserializer;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@JsonDeserialize(using = TableUpdateDeserializer.class)
public class TableUpdate {

  private final Map<UUID, RowUpdate> rowUpdates;

  public TableUpdate(Map<UUID, RowUpdate> rowUpdates) {
    this.rowUpdates = rowUpdates;
  }

  public Map<UUID, RowUpdate> getRowUpdates() {
    return rowUpdates;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TableUpdate)) {
      return false;
    }
    TableUpdate that = (TableUpdate) other;
    return Objects.equals(rowUpdates, that.getRowUpdates());
  }

  @Override
  public int hashCode() {
    return Objects.hash(rowUpdates);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "rowUpdates=" + rowUpdates
        + "]";
  }
}
