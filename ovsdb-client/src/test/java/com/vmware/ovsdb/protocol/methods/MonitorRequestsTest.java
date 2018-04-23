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
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitorRequestsTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    String expectedResult = "{}";
    MonitorRequests monitorRequests = new MonitorRequests(ImmutableMap.of());
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequests));

    expectedResult
        = "{\"Logical_Switch\":{\"columns\":[\"name\",\"tunnel_key\"]},"
        + "\"Physical_Switch\":{\"columns\":[\"name\",\"ports\"],"
        + "\"select\":{\"initial\":true,\"insert\":false,"
        + "\"delete\":true}}}";
    monitorRequests = new MonitorRequests(
        ImmutableMap.of(
            "Logical_Switch",
            new MonitorRequest(ImmutableList.of("name", "tunnel_key"), null),
            "Physical_Switch", new MonitorRequest(
                ImmutableList.of("name", "ports"),
                new MonitorSelect(true, false, true, null)
            )
        )
    );
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequests));
  }

  @Test
  public void testEquals() {
    String tableName1 = "table1";
    String tableName2 = "table2";
    List<String> columns1 = ImmutableList.of("column1", "column2");
    List<String> columns2 = Stream.of("column1", "column2").collect(Collectors.toList());
    MonitorSelect monitorSelect1 = new MonitorSelect().setInitial(true).setInsert(false);
    MonitorSelect monitorSelect2 = new MonitorSelect(true, false, null, null);

    MonitorRequest monitorRequest11 = new MonitorRequest();
    MonitorRequest monitorRequest12 = new MonitorRequest(null, null);

    MonitorRequest monitorRequest21 = new MonitorRequest(columns1, monitorSelect1);
    MonitorRequest monitorRequest22 = new MonitorRequest(columns2, monitorSelect2);

    MonitorRequests monitorRequests11 =
        new MonitorRequests(
            ImmutableMap.of(tableName1, monitorRequest11, tableName2, monitorRequest21));

    Map<String, MonitorRequest> monitorRequestMap = new HashMap<>();
    monitorRequestMap.put(tableName1, monitorRequest12);
    monitorRequestMap.put(tableName2, monitorRequest22);
    MonitorRequests monitorRequests12 =
        new MonitorRequests(monitorRequestMap);

    new EqualsTester()
        .addEqualityGroup(monitorRequests11, monitorRequests12)
        .testEquals();
  }
}
