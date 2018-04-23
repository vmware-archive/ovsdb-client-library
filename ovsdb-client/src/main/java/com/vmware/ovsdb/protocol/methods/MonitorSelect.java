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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Representation of {@literal <monitor-select>}.
 *
 * <pre>
 * {@literal <monitor-select>} is an object with the following members:
 *
 * "initial":{@literal <boolean>}              optional
 * "insert": {@literal <boolean>}              optional
 * "delete": {@literal <boolean>}              optional
 * "modify": {@literal <boolean>}              optional
 * </pre>
 */
public class MonitorSelect {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean initial = null;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean insert = null;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean delete = null;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean modify = null;

  /**
   * Create a {@link MonitorSelect} object with all fields being default values.
   */
  public MonitorSelect() {

  }

  /**
   * Create a {@link MonitorSelect} object.
   *
   * @param initial value of the "initial" field
   * @param insert value of the "insert" field
   * @param delete value of the "delete" field
   * @param modify value of the "modify" field
   */
  public MonitorSelect(Boolean initial, Boolean insert, Boolean delete, Boolean modify) {
    this.initial = initial;
    this.insert = insert;
    this.delete = delete;
    this.modify = modify;
  }

  public Boolean getInitial() {
    return initial;
  }

  public MonitorSelect setInitial(Boolean initial) {
    this.initial = initial;
    return this;
  }

  public Boolean getInsert() {
    return insert;
  }

  public MonitorSelect setInsert(Boolean insert) {
    this.insert = insert;
    return this;
  }

  public Boolean getDelete() {
    return delete;
  }

  public MonitorSelect setDelete(Boolean delete) {
    this.delete = delete;
    return this;
  }

  public Boolean getModify() {
    return modify;
  }

  public MonitorSelect setModify(Boolean modify) {
    this.modify = modify;
    return this;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof MonitorSelect)) {
      return false;
    }
    MonitorSelect that = (MonitorSelect) other;
    return Objects.equals(initial, that.getInitial())
        && Objects.equals(insert, that.getInsert())
        && Objects.equals(delete, that.getDelete())
        && Objects.equals(modify, that.getModify());
  }

  @Override
  public int hashCode() {
    return Objects.hash(initial, insert, delete, modify);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "initial=" + initial
        + ", insert=" + insert
        + ", delete=" + delete
        + ", modify=" + modify
        + "]";
  }
}
