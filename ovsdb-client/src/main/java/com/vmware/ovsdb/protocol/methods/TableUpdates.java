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
import com.vmware.ovsdb.protocol.methods.deserializer.TableUpdatesDeserializer;

import java.util.Map;
import java.util.Objects;

@JsonDeserialize(using = TableUpdatesDeserializer.class)
public class TableUpdates {

  private final Map<String, TableUpdate> tableUpdates;

  public TableUpdates(Map<String, TableUpdate> tableUpdates) {
    this.tableUpdates = tableUpdates;
  }

  public Map<String, TableUpdate> getTableUpdates() {
    return tableUpdates;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TableUpdates)) {
      return false;
    }
    TableUpdates that = (TableUpdates) other;
    return Objects.equals(tableUpdates, that.getTableUpdates());
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableUpdates);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "tableUpdates=" + tableUpdates
        + "]";
  }
}
