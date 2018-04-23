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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vmware.ovsdb.protocol.operation.notation.deserializer.AtomDeserializer;
import com.vmware.ovsdb.protocol.operation.notation.serializer.AtomSerializer;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of {@literal <atom>}.
 *
 * <pre>
 * {@literal
 * <atom>
 *   A JSON value that represents a scalar value for a column, one of
 *   <string>, <number>, <boolean>, <uuid>, or <named-uuid>.
 * }
 * </pre>
 */
@JsonSerialize(using = AtomSerializer.class)
@JsonDeserialize(using = AtomDeserializer.class)
public class Atom<T> extends Value {

  private T value;

  public Atom(T value) {
    this.value = value;
  }

  public static Atom<String> string(String value) {
    return new Atom<>(value);
  }

  public static Atom<Long> integer(long value) {
    return new Atom<>(value);
  }

  public static Atom<Double> real(Double value) {
    return new Atom<>(value);
  }

  public static Atom<Boolean> bool(boolean value) {
    return new Atom<>(value);
  }

  public static Atom<Uuid> uuid(Uuid value) {
    return new Atom<>(value);
  }

  public static Atom<Uuid> uuid(UUID value) {
    return new Atom<>(new Uuid(value));
  }

  public static Atom<NamedUuid> namedUuid(NamedUuid value) {
    return new Atom<>(value);
  }

  public static Atom<NamedUuid> namedUuid(String value) {
    return new Atom<>(new NamedUuid(value));
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Atom)) {
      return false;
    }
    Atom<?> atom = (Atom<?>) other;
    return Objects.equals(value, atom.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
