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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <pre>
 * {@literal <monitor-select>} is an object with the following members:
 *
 * "initial":{@literal <boolean>}              optional
 * "insert": {@literal <boolean>}              optional
 * "delete": {@literal <boolean>}              optional
 * "modify": {@literal <boolean>}              optional
 * </pre>
 */
public class MonitorSelect {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean initial = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean insert = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean delete = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean modify = null;

    public MonitorSelect() {

    }

    public MonitorSelect(
        Boolean initial, Boolean insert, Boolean delete, Boolean modify
    ) {
        this.initial = initial;
        this.insert = insert;
        this.delete = delete;
        this.modify = modify;
    }

    public Boolean isInitial() {
        return initial;
    }

    public void setInitial(Boolean initial) {
        this.initial = initial;
    }

    public Boolean isInsert() {
        return insert;
    }

    public void setInsert(Boolean insert) {
        this.insert = insert;
    }

    public Boolean isDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean isModify() {
        return modify;
    }

    public void setModify(Boolean modify) {
        this.modify = modify;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "initial=" + initial
            + ", insert=" + insert
            + ", delete=" + delete
            + ", modify=" + modify
            + "]";
    }
}
