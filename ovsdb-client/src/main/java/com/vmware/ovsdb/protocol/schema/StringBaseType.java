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
 * Represent a <base-type> with a string <atomic-type> as it's type.
 * }
 * </pre>
 *
 * @see BaseType
 * @see AtomicType
 */
public class StringBaseType extends BaseType {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long minLength;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long maxLength;

    public StringBaseType() {
        this(null, null);
    }

    public StringBaseType(Value enums) {
        super(AtomicType.STRING, enums);
        this.minLength = null;
        this.maxLength = null;
    }

    public StringBaseType(Long minLength, Long maxLength) {
        super(AtomicType.STRING);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public Long getMinLength() {
        return minLength;
    }

    public Long getMaxLength() {
        return maxLength;
    }

    @Override
    public int hashCode() {
        int result = minLength != null
            ? minLength.hashCode()
            : 0;
        result = 31 * result + (maxLength != null
            ? maxLength.hashCode()
            : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringBaseType)) {
            return false;
        }

        StringBaseType that = (StringBaseType) o;

        if (minLength != null
            ? !minLength.equals(that.minLength)
            : that.minLength != null) {
            return false;
        }
        return maxLength != null
            ? maxLength.equals(that.maxLength)
            : that.maxLength == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "minLength=" + minLength
            + ", maxLength=" + maxLength
            + "]";
    }
}
