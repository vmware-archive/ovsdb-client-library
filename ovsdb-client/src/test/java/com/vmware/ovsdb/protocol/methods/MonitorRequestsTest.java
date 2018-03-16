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

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

public class MonitorRequestsTest {

    @Test
    public void testSerialization() throws JsonProcessingException {
        String expectedResult = "{}";
        MonitorRequests monitorRequests = new MonitorRequests(
            ImmutableMap.of());
        assertEquals(
            expectedResult, JsonUtil.serialize(monitorRequests));

        expectedResult
            = "{\"Logical_Switch\":{\"columns\":[\"name\",\"tunnel_key\"]},"
            + "\"Physical_Switch\":{\"columns\":[\"name\",\"ports\"],"
            + "\"select\":{\"initial\":true,\"insert\":false,"
            + "\"delete\":true}}}";
        monitorRequests = new MonitorRequests(
            ImmutableMap.of(
                "Logical_Switch",
                new MonitorRequest(
                    ImmutableList.of("name", "tunnel_key"),
                    null
                ),
                "Physical_Switch", new MonitorRequest(
                    ImmutableList.of("name", "ports"),
                    new MonitorSelect(true, false, true, null)
                )
            )
        );
        assertEquals(
            expectedResult, JsonUtil.serialize(monitorRequests));
    }
}
