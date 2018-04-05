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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.operation.notation.deserializer.SetDeserializer;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Representation of {@literal <set>}.
 *
 * <pre>
 * {@literal
 * <set>
 *   Either an <atom>, representing a set with exactly one element, or
 *   a 2-element JSON array that represents a database set value.  The
 *   first element of the array must be the string "set", and the
 *   second element must be an array of zero or more <atom>s giving the
 *   values in the set.  All of the <atom>s must have the same type.
 *
 * In this implementation, Set is only a 2-element JSON array. Because in the
 * com.vmware.ovsdb.protocol, <set> only appears in <value> and <value> can be one of <atom>,
 * <set>, or <map>. Thus there is no need to define <set> as either <atom> or
 * an array.
 * }
 * </pre>
 */
@JsonDeserialize(using = SetDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Set extends Value {

  public String setString = OvsdbConstant.SET; // For serializing

  private java.util.Set<Atom> set;

  public Set() {
    this(new HashSet<>());
  }

  public Set(java.util.Set<Atom> set) {
    this.set = set;
  }

  public static Set of(Object... elements) {
    return new Set(Arrays.stream(elements).map(Atom::new).collect(
        Collectors.toSet()));
  }

  public static Set of(Atom... elements) {
    return new Set(Arrays.stream(elements).collect(Collectors.toSet()));
  }

  /**
   * Create a {@link Set} object using a {@link java.util.Set} object.
   *
   * @param set value of the set
   */
  public static <T> Set of(java.util.Set<T> set) {
    if (set == null) {
      return null;
    }
    return new Set(
        set.stream().map(Atom::new).collect(
            Collectors.toSet())
    );
  }

  public java.util.Set<Atom> getSet() {
    return set;
  }

  public void setSet(java.util.Set<Atom> set) {
    this.set = set;
  }

  public void addValue(Atom value) {
    set.add(value);
  }

  public void removeValue(Atom value) {
    set.remove(value);
  }

  @Override
  public int hashCode() {
    return set != null
        ? set.hashCode()
        : 0;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Set)) {
      return false;
    }

    Set uuid1 = (Set) other;

    return set != null
        ? set.equals(uuid1.set)
        : uuid1.set == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + set;
  }
}
