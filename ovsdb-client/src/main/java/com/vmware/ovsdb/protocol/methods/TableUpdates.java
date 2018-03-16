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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.methods.deserializer.TableUpdatesDeserializer;
import java.util.Map;

@JsonDeserialize(using = TableUpdatesDeserializer.class)
public class TableUpdates {

    private final Map<String, TableUpdate> tableUpdates;

    public TableUpdates(
        Map<String, TableUpdate> tableUpdates
    ) {
        this.tableUpdates = tableUpdates;
    }

    public Map<String, TableUpdate> getTableUpdates() {
        return tableUpdates;
    }

    @Override
    public int hashCode() {
        return tableUpdates != null
            ? tableUpdates.hashCode()
            : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableUpdates)) {
            return false;
        }

        TableUpdates that = (TableUpdates) o;

        return tableUpdates != null
            ? tableUpdates.equals(that.tableUpdates)
            : that.tableUpdates == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "tableUpdates=" + tableUpdates
            + "]";
    }
}
