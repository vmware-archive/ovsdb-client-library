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

package com.vmware.ovsdb.jsonrpc.v1.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.model.JsonRpcV1Request;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class JsonUtilTest {

  @Test
  public void testTreeAndValueConversion() throws IOException {
    String string = "string";

    assertEquals(string, JsonUtil.treeToValue(JsonUtil.toJsonNode(string), String.class));

    JsonRpcV1Request request = new JsonRpcV1Request("id", "method");
    assertEquals(
        request, JsonUtil.treeToValue(JsonUtil.toJsonNode(request), JsonRpcV1Request.class)
    );

    JsonNode jsonNode = JsonUtil.readTree("{}");
    assertNull(JsonUtil.treeToValueNoException(jsonNode, JsonRpcV1Request.class));

    assertNull(JsonUtil.treeToValueNoException(jsonNode, new TypeReference<String>() {}));
  }

  @Test
  public void testDeserialize() throws IOException {
    String jsonString = "{\"id\":\"id\", \"method\":\"method\", \"params\":[]}";
    JsonRpcV1Request request = new JsonRpcV1Request("id", "method");
    assertEquals(
        request, JsonUtil.deserialize(jsonString, JsonRpcV1Request.class)
    );
    String tmpFile = "tempfile";
    Path file = Paths.get(tmpFile);
    Files.write(file, Collections.singletonList(jsonString));
    assertEquals(
        request, JsonUtil.deserialize(file.toUri().toURL(), JsonRpcV1Request.class)
    );
    Files.delete(file);

    assertNull(JsonUtil.deserializeNoException("{}", JsonRpcV1Request.class));
  }

  @Test
  public void testSerialize() throws JsonProcessingException {
    String jsonString = "{\"method\":\"method\",\"params\":[],\"id\":\"id\"}";
    JsonRpcV1Request request = new JsonRpcV1Request("id", "method");
    assertEquals(
        jsonString, JsonUtil.serialize(request)
    );

    class WrongClass {
      private String field;
    }
    assertNull(JsonUtil.serializeNoException(new WrongClass()));
  }
}
