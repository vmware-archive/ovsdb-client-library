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
import com.vmware.ovsdb.protocol.operation.notation.deserializer.ConditionDeserializer;

/**
 * <pre>
 * {@literal
 * <condition>
 *   A 3-element JSON array of the form [<column>, <function>, <value>]
 *   that represents a test on a column value.  Except as otherwise
 *   specified below, <value> MUST have the same type as <column>.  The
 *   meaning depends on the type of <column>:
 *
 *   integer or real
 *     <function> must be "<", "<=", "==", "!=", ">=", ">",
 *     "includes", or "excludes".
 *
 *     The test is true if the column's value satisfies the relation
 *     <function> <value>, e.g., if the column has value 1 and <value>
 *     is 2, the test is true if <function> is "<", "<=", or "!=", but
 *     not otherwise.
 *
 *     "includes" is equivalent to "=="; "excludes" is equivalent to
 *     "!=".
 *
 *   boolean or string or uuid
 *     <function> must be "!=", "==", "includes", or "excludes".
 *
 *     If <function> is "==" or "includes", the test is true if the
 *     column's value equals <value>.  If <function> is "!=" or
 *     "excludes", the test is inverted.
 *
 *   set or map
 *     <function> must be "!=", "==", "includes", or "excludes".
 *
 *     If <function> is "==", the test is true if the column's value
 *     contains exactly the same values (for sets) or pairs (for
 *     maps).  If <function> is "!=", the test is inverted.
 *
 *     If <function> is "includes", the test is true if the column's
 *     value contains all of the values (for sets) or pairs (for maps)
 *     in <value>.  The column's value may also contain other values
 *     or pairs.
 *
 *     If <function> is "excludes", the test is true if the column's
 *     value does not contain any of the values (for sets) or pairs
 *     (for maps) in <value>.  The column's value may contain other
 *     values or pairs not in <value>.
 *
 *     If <function> is "includes" or "excludes", then the required
 *     type of <value> is slightly relaxed, in that it may have fewer
 *     than the minimum number of elements specified by the column's
 *     type.  If <function> is "excludes", then the required type is
 *     additionally relaxed in that <value> may have more than the
 *     maximum number of elements specified by the column's type.
 * }
 * </pre>
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonDeserialize(using = ConditionDeserializer.class)
public class Condition {

    private final String column;

    private final Function function;

    private final Value value;

    public Condition(String column, Function function, Value value) {
        this.column = column;
        this.function = function;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Function getFunction() {
        return function;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int result = column != null
            ? column.hashCode()
            : 0;
        result = 31 * result + (function != null
            ? function.hashCode()
            : 0);
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
        if (!(o instanceof Condition)) {
            return false;
        }

        Condition condition = (Condition) o;

        if (column != null
            ? !column.equals(condition.column)
            : condition.column != null) {
            return false;
        }
        if (function != condition.function) {
            return false;
        }
        return value != null
            ? value.equals(condition.value)
            : condition.value == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "column=" + column
            + ", function=" + function
            + ", value=" + value
            + "]";
    }
}
