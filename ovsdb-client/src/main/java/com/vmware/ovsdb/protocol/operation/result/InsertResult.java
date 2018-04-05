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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;

/**
 * Result of the "insert" operation, which only contains one "uuid" field.
 */
@JsonDeserialize
public class InsertResult extends OperationResult {

  private Uuid uuid;

  @JsonCreator
  public InsertResult(@JsonProperty(value = "uuid", required = true) Uuid uuid) {
    this.uuid = uuid;
  }

  public Uuid getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof InsertResult)) {
      return false;
    }

    InsertResult that = (InsertResult) other;

    return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
  }

  @Override
  public int hashCode() {
    return uuid != null ? uuid.hashCode() : 0;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "uuid=" + uuid
        + "]";
  }
}
