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

/**
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

    private Atom<K> key;

    private Atom<V> value;

    public Pair(K key, V value) {
        this.key = new Atom<>(key);
        this.value = new Atom<>(value);
    }

    public Pair(Atom<K> key, Atom<V> value) {
        this.key = key;
        this.value = value;
    }

    public Atom<K> getKey() {
        return key;
    }

    public void setKey(Atom<K> key) {
        this.key = key;
    }

    public Atom<V> getValue() {
        return value;
    }

    public void setValue(Atom<V> value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int result = key != null
            ? key.hashCode()
            : 0;
        result = 31 * result + (value != null
            ? value.hashCode()
            : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (key != null
            ? !key.equals(pair.key)
            : pair.key != null) {
            return false;
        }
        return value != null
            ? value.equals(pair.value)
            : pair.value == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "key=" + key
            + ", value=" + value
            + "]";
    }
}
