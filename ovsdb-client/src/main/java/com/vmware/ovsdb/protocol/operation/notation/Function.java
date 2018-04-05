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

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of {@literal <function>}.
 *
 * <pre>
 * {@literal
 * <function>
 *    One of "<", "<=", "==", "!=", ">=", ">", "includes", or "excludes".
 * }
 * </pre>
 */
public enum Function {
  LESS_THAN("<"),
  LESS_THAN_OR_EQUALS("<="),
  EQUALS("=="),
  NOT_EQUALS("!="),
  GREATER_THAN(">"),
  GREATER_THAN_OR_EQUALS(">="),
  INCLUDES("includes"),
  EXCLUDES("excludes");

  private static final Map<String, Function> lookup = new HashMap<>();

  static {
    for (Function function : Function.values()) {
      lookup.put(function.toString(), function);
    }
  }

  private String name;

  Function(String name) {
    this.name = name;
  }

  public static Function fromString(String function) {
    return lookup.get(function);
  }

  @JsonValue
  public String toString() {
    return name;
  }
}
