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

package com.vmware.ovsdb.protocol.operation.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for "update", "mutate" and "delete" operation result. It contains only one "count"
 * field.
 */
public class UpdateResult extends OperationResult {

    private long count;

    @JsonCreator
    public UpdateResult(@JsonProperty(value = "count", required = true) long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpdateResult)) {
            return false;
        }

        UpdateResult that = (UpdateResult) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return (int) (count ^ (count >>> 32));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "count=" + count
            + "]";
    }
}
