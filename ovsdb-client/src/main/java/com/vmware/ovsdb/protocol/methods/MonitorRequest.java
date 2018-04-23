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

import java.util.List;
import java.util.Objects;

/**
 * Representation of {@literal <monitor-request>}.
 *
 * <pre>
 * {@literal <monitor-request>} is an object with the following members:
 *
 * "columns": [{@literal <column>}*]            optional
 * "select": {@literal <monitor-select>}        optional
 * </pre>
 */
public class MonitorRequest {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final List<String> columns;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final MonitorSelect select;

  /**
   * Create an {@link MonitorRequest} object with all fields being default values.
   */
  public MonitorRequest() {
    this(null, null);
  }

  /**
   * Create an {@link MonitorRequest} object with select being default value.
   *
   * @param columns value of the "columns" field
   */
  public MonitorRequest(List<String> columns) {
    this(columns, null);
  }

  /**
   * Create an {@link MonitorRequest} object with columns being default value.
   *
   * @param select value of the "select" field
   */
  public MonitorRequest(MonitorSelect select) {
    this(null, select);
  }

  /**
   * Create an {@link MonitorRequest} object.
   *
   * @param columns value of the "columns" field
   * @param select value of the "select" field
   */
  public MonitorRequest(List<String> columns, MonitorSelect select) {
    this.columns = columns;
    this.select = select;
  }

  public List<String> getColumns() {
    return columns;
  }

  public MonitorSelect getSelect() {
    return select;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof MonitorRequest)) {
      return false;
    }
    MonitorRequest that = (MonitorRequest) other;
    return Objects.equals(columns, that.getColumns())
        && Objects.equals(select, that.getSelect());
  }

  @Override
  public int hashCode() {
    return Objects.hash(columns, select);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "columns=" + columns
        + ", select=" + select
        + "]";
  }
}
