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

package com.vmware.ovsdb.protocol.operation.notation;

import static org.junit.Assert.assertEquals;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.io.IOException;
import org.junit.Test;

public class ConditionTest {

  private final String jsonString1 = "[\"name\",\"==\",\"ls1\"]";

  private final Condition condition1 = new Condition(
      "name", Function.EQUALS, Atom.string("ls1"));

  private final String jsonString2
      = "[\"numbers\",\"includes\",[\"set\",[1,2,3,4]]]";

  private final Condition condition2 = new Condition(
      "numbers", Function.INCLUDES, Set.of(1L, 2L, 3L, 4L)
  );

  @Test
  public void testSerialization() throws JsonProcessingException {
    assertEquals(jsonString1, JsonUtil.serialize(condition1));
    assertEquals(jsonString2, JsonUtil.serialize(condition2));
  }

  @Test
  public void testDeserialization() throws IOException {
    assertEquals(
        condition1, JsonUtil.deserialize(jsonString1, Condition.class));

    assertEquals(
        condition2, JsonUtil.deserialize(jsonString2, Condition.class));
  }

  @Test(expected = IOException.class)
  public void testInvalidCondition1() throws IOException {
    JsonUtil.deserialize("[\"name\"]", Condition.class);
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(new Condition("name", Function.EQUALS, Atom.string("name1")),
            new Condition("name", Function.EQUALS, new Atom<>("name1")))
        .testEquals();
  }
}
