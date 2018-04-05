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

package com.vmware.ovsdb.protocol.operation.notation;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <pre>
 * {@literal
 * <mutator>
 *    One of "+=", "-=", "*=", "/=", "%=", "insert", or "delete".
 * }
 * </pre>
 */
public enum Mutator {
  SUM("+="),
  DIFFERENCE("-="),
  PRODUCT("*="),
  QUOTIENT("/="),
  REMINDER("%="),
  INSERT("insert"),
  DELETE("delete");

  private String name;

  Mutator(String name) {
    this.name = name;
  }

  @JsonValue
  public String toString() {
    return name;
  }
}
