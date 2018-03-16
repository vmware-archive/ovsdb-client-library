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

package com.vmware.ovsdb.protocol.methods;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <pre>
 * {@literal
 * For a "lock" operation, the "locked" member in the response object is
 * true if the lock has already been acquired and false if another
 * client holds the lock and the client's request for it was queued.  In
 * the latter case, the client will be notified later with a "locked"
 * message (Section 4.1.9) when acquisition succeeds.
 * }
 * </pre>
 */
public class LockResult {

    private boolean locked;

    @JsonCreator
    public LockResult(@JsonProperty(value = "locked", required = true) boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LockResult)) {
            return false;
        }

        LockResult that = (LockResult) o;

        return locked == that.locked;
    }

    @Override
    public int hashCode() {
        return (locked ? 1 : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "locked=" + locked
            + "]";
    }
}
