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
 * Represent a <base-type> with a real <atomic-type> as it's type.
 * }
 * </pre>
 *
 * @see BaseType
 * @see AtomicType
 */
public class RealBaseType extends BaseType {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Double minReal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Double maxReal;

    public RealBaseType() {
        this(null, null);
    }

    public RealBaseType(Value enums) {
        super(AtomicType.REAL, enums);
        this.minReal = null;
        this.maxReal = null;
    }

    public RealBaseType(Double minReal, Double maxReal) {
        super(AtomicType.REAL);
        this.minReal = minReal;
        this.maxReal = maxReal;
    }

    public Double getMinReal() {
        return minReal;
    }

    public Double getMaxReal() {
        return maxReal;
    }

    @Override
    public int hashCode() {
        int result = minReal != null
            ? minReal.hashCode()
            : 0;
        result = 31 * result + (maxReal != null
            ? maxReal.hashCode()
            : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RealBaseType)) {
            return false;
        }

        RealBaseType that = (RealBaseType) o;

        if (minReal != null
            ? !minReal.equals(that.minReal)
            : that.minReal != null) {
            return false;
        }
        return maxReal != null
            ? maxReal.equals(that.maxReal)
            : that.maxReal == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "minReal=" + minReal
            + ", maxReal=" + maxReal
            + "]";
    }
}
