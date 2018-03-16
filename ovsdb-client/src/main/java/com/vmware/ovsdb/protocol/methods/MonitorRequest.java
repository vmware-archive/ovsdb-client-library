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
import java.util.List;

/**
 * <pre>
 * {@literal <monitor-request>} is an object with the following members:
 *
 * "columns": [{@literal <column>}*]            optional
 * "select": {@literal <monitor-select>}        optional
 * </pre>
 */
public class MonitorRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> columns;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MonitorSelect select;

    public MonitorRequest() {

    }

    public MonitorRequest(List<String> columns, MonitorSelect select) {
        this.columns = columns;
        this.select = select;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public MonitorSelect getSelect() {
        return select;
    }

    public void setSelect(MonitorSelect select) {
        this.select = select;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "columns=" + columns
            + ", select=" + select
            + "]";
    }
}
