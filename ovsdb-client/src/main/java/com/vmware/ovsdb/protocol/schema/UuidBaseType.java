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

package com.vmware.ovsdb.protocol.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.ovsdb.protocol.operation.notation.Value;

/**
 * <pre>
 * {@literal
 * Represent a <base-type> with a uuid <atomic-type> as it's type.
 * }
 * </pre>
 *
 * @see BaseType
 * @see AtomicType
 */
public class UuidBaseType extends BaseType {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String refTable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String refType;

    public UuidBaseType() {
        this(null, null);
    }

    public UuidBaseType(Value enums) {
        super(AtomicType.UUID, enums);
        this.refTable = null;
        this.refType = null;
    }

    public UuidBaseType(String refTable, String refType) {
        super(AtomicType.UUID);
        this.refTable = refTable;
        this.refType = refType;
    }

    public String getRefTable() {
        return refTable;
    }

    public String getRefType() {
        return refType;
    }

    @Override
    public int hashCode() {
        int result = refTable != null
            ? refTable.hashCode()
            : 0;
        result = 31 * result + (refType != null
            ? refType.hashCode()
            : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UuidBaseType)) {
            return false;
        }

        UuidBaseType that = (UuidBaseType) o;

        if (refTable != null
            ? !refTable.equals(that.refTable)
            : that.refTable != null) {
            return false;
        }
        return refType != null
            ? refType.equals(that.refType)
            : that.refType == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "refTable=" + refTable
            + ", refType=" + refType
            + "]";
    }
}
