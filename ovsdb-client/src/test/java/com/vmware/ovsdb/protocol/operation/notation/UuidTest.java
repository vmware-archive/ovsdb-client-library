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
import java.util.UUID;
import org.junit.Test;

public class UuidTest {

  private static final Uuid uuid = new Uuid(UUID.randomUUID());

  private static final String json = "[\"uuid\",\"" + uuid.getUuid() + "\"]";

  @Test
  public void testSerialization() throws JsonProcessingException {
    assertEquals(json, JsonUtil.serialize(uuid));
  }

  @Test
  public void testDeserialization() throws IOException {
    assertEquals(uuid, JsonUtil.deserialize(json, Uuid.class));
  }

  @Test(expected = IOException.class)
  public void testInvalidUuid1() throws IOException {
    String json = "[\"id\", \"" + uuid + "\"]";

    JsonUtil.deserialize(json, Uuid.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidUuid2() throws IOException {
    String json = "[\"" + uuid + "\"]";

    JsonUtil.deserialize(json, Uuid.class);
  }

  @Test(expected = IOException.class)
  public void testInvalidUuid3() throws IOException {
    String json = "[\"uuid\", \"123\"]";

    JsonUtil.deserialize(json, Uuid.class);
  }

  @Test
  public void testEquals() {
    new EqualsTester().addEqualityGroup(uuid, Uuid.of(uuid.getUuid())).testEquals();
  }
}
