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
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitorRequestTest {

  @Test
  public void testSerialization() throws JsonProcessingException {
    String expectedResult = "{}";
    MonitorRequest monitorRequest = new MonitorRequest();
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequest));

    // Empty columns and select
    expectedResult = "{\"columns\":[],\"select\":{}}";
    monitorRequest = new MonitorRequest(
        ImmutableList.of(), new MonitorSelect());
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequest));

    // Non-empty columns and select
    expectedResult
        = "{\"columns\":[\"name\",\"description\"],"
        + "\"select\":{\"initial\":true}}";
    monitorRequest = new MonitorRequest(
        ImmutableList.of("name", "description"),
        new MonitorSelect(true, null, null, null)
    );
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequest));

    // No columns field
    expectedResult
        = "{\"select\":{\"initial\":true}}";
    monitorRequest = new MonitorRequest(new MonitorSelect().setInitial(true));
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequest));

    // No select field
    expectedResult
        = "{\"columns\":[\"name\",\"description\"]}";
    monitorRequest = new MonitorRequest(
        ImmutableList.of("name", "description")
    );
    assertEquals(expectedResult, JsonUtil.serialize(monitorRequest));
  }

  @Test
  public void testEquals() {
    List<String> columns1 = ImmutableList.of("column1", "column2");
    List<String> columns2 = Stream.of("column1", "column2").collect(Collectors.toList());
    MonitorSelect monitorSelect1 = new MonitorSelect().setInitial(true).setInsert(false);
    MonitorSelect monitorSelect2 = new MonitorSelect(true, false, null, null);

    new EqualsTester()
        .addEqualityGroup(new MonitorRequest(), new MonitorRequest(null, null))
        .addEqualityGroup(new MonitorRequest(columns1), new MonitorRequest(columns2, null))
        .addEqualityGroup(new MonitorRequest(columns1, monitorSelect1),
            new MonitorRequest(columns2, monitorSelect2))
        .testEquals();
  }
}
