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
import com.vmware.ovsdb.protocol.operation.notation.deserializer.PairDeserializer;

import java.util.Objects;

/**
 * Representation of {@literal <pair>}.
 *
 * <pre>
 * {@literal
 * <pair>
 *   A 2-element JSON array that represents a pair within a database
 *   map.  The first element is an <atom> that represents the key, and
 *   the second element is an <atom> that represents the value.
 * }
 * </pre>
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonDeserialize(using = PairDeserializer.class)
public class Pair<K, V> {

  private final Atom<K> key;

  private final Atom<V> value;

  public Pair(Atom<K> key, Atom<V> value) {
    this.key = key;
    this.value = value;
  }

  public Atom<K> getKey() {
    return key;
  }

  public Atom<V> getValue() {
    return value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Pair)) {
      return false;
    }
    Pair<?, ?> that = (Pair<?, ?>) other;
    return Objects.equals(key, that.getKey())
        && Objects.equals(value, that.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "key=" + key
        + ", value=" + value
        + "]";
  }
}
