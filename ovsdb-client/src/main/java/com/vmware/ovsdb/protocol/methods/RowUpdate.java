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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.ovsdb.protocol.operation.notation.Row;

public class RowUpdate {

    private Row old;

    @JsonProperty(value = "new")
    private Row _new;

    public RowUpdate() {
    }

    public RowUpdate(Row old, Row _new) {
        this.old = old;
        this._new = _new;
    }

    public Row getOld() {
        return old;
    }

    public void setOld(Row old) {
        this.old = old;
    }

    public Row getNew() {
        return _new;
    }

    public void setNew(Row _new) {
        this._new = _new;
    }

    @Override
    public int hashCode() {
        int result = old != null
            ? old.hashCode()
            : 0;
        result = 31 * result + (_new != null
            ? _new.hashCode()
            : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RowUpdate)) {
            return false;
        }

        RowUpdate rowUpdate = (RowUpdate) o;

        if (old != null
            ? !old.equals(rowUpdate.old)
            : rowUpdate.old != null) {
            return false;
        }
        return _new != null
            ? _new.equals(rowUpdate._new)
            : rowUpdate._new == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "old=" + old
            + ", _new=" + _new
            + "]";
    }
}
