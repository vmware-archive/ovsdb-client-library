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

/**
 * <pre>
 * {@literal
 * <mutation>
 *    A 3-element JSON array of the form [<column>, <mutator>, <value>]
 *    that represents a change to a column value.  Except as otherwise
 *    specified below, <value> must have the same type as <column>.  The
 *    meaning depends on the type of <column>:
 *
 *    integer or real
 *       <mutator> must be "+=", "-=", "*=", "/=", or (integer only)
 *       "%=".  The value of <column> is changed to the sum, difference,
 *       product, quotient, or remainder, respectively, of <column> and
 *       <value>.
 *
 *       Constraints on <column> are ignored when parsing <value>.
 *
 *    boolean, string, or uuid
 *       No valid <mutator>s are currently defined for these types.
 *
 *    set
 *       Any <mutator> valid for the set's element type may be applied
 *       to the set, in which case the mutation is applied to each
 *       member of the set individually. <value> must be a scalar value
 *       of the same type as the set's element type, except that
 *       constraints are ignored when parsing <value>.
 *
 *       If <mutator> is "insert", then each of the values in the set in
 *       <value> is added to <column> if it is not already present.  The
 *       required type of <value> is slightly relaxed, in that it may
 *       have fewer than the minimum number of elements specified by the
 *       column's type.
 *
 *       If <mutator> is "delete", then each of the values in the set in
 *       <value> is removed from <column> if it is present there.  The
 *       required type is slightly relaxed in that <value> may have more
 *       or less than the maximum number of elements specified by the
 *       column's type.
 *
 *    map
 *       <mutator> must be "insert" or "delete".
 *
 *       If <mutator> is "insert", then each of the key-value pairs in
 *       the map in <value> is added to <column> only if its key is not
 *       already present.  The required type of <value> is slightly
 *       relaxed, in that it may have fewer than the minimum number of
 *       elements specified by the column's type.
 *
 *       If <mutator> is "delete", then <value> may have the same type
 *       as <column> (a map type), or it may be a set whose element type
 *       is the same as <column>'s key type:
 *
 *       +  If <value> is a map, the mutation deletes each key-value
 *          pair in <column> whose key and value equal one of the key-
 *          value pairs in <value>.
 *
 *       +  If <value> is a set, the mutation deletes each key-value
 *          pair in <column> whose key equals one of the values in
 *          <value>.
 *
 *       For "delete", <value> may have any number of elements,
 *       regardless of restrictions on the number of elements in
 *       <column>.
 * }
 * </pre>
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Mutation {

    private final String column;

    private final Mutator mutator;

    private final Value value;

    public Mutation(String column, Mutator mutator, Value value) {
        this.column = column;
        this.mutator = mutator;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Mutator getMutator() {
        return mutator;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "column=" + column
            + ", mutator=" + mutator
            + ", value=" + value
            + "]";
    }
}
