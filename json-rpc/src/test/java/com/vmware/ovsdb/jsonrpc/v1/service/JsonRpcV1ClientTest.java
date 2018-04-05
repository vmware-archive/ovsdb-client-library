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

package com.vmware.ovsdb.jsonrpc.v1.service;

import static com.vmware.ovsdb.jsonrpc.v1.util.TestUtil.getRequestNode;
import static com.vmware.ovsdb.jsonrpc.v1.util.TestUtil.getResponseNode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcConnectionClosedException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcDuplicateIdException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ClientImpl;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Before;
import org.junit.Test;

public class JsonRpcV1ClientTest {

  private static final int TIMEOUT = 3; // 3 seconds

  private static final int MAX_TIMEOUT = TIMEOUT * 2;
  private static Integer counter = 0;
  private final JsonRpcTransporter transporter = mock(JsonRpcTransporter.class);
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private JsonRpcV1Client jsonRpcV1Client;

  @Before
  public void setUp() {
    jsonRpcV1Client = new JsonRpcV1ClientImpl(transporter, scheduler, MAX_TIMEOUT,
        TimeUnit.SECONDS);
  }

  @Test
  public void testServerResponseOnTime()
      throws JsonRpcException, TimeoutException, ExecutionException {
    String[] expectedResult = {
        "list_methods",
        "add",
        "sub",
        "mul",
        "echo",
        "print"
    };
    String id = getNextId();
    String method = "list_methods";

    JsonNode request = getRequestNode(id, method);

    setUpMockTransporter(
        expectedResult, null, id, request, randomDelayWithinTimeout());

    String[] result = syncCall(id, method, String[].class);
    assertArrayEquals(expectedResult, result);
  }

  @Test(expected = TimeoutException.class)
  public void testServerResponseLate()
      throws JsonRpcException, ExecutionException, TimeoutException {
    String id = getNextId();
    String method = "list_methods";

    JsonNode request = getRequestNode(id, method);

    setUpMockTransporter(
        null, null, id, request, TIMEOUT + 1);

    syncCall(id, method, String[].class);
  }

  @Test(expected = TimeoutException.class)
  public void testServerNoResponse()
      throws JsonRpcException, ExecutionException, TimeoutException {
    String id = getNextId();
    doNothing().when(transporter).send(any());
    syncCall(id, "method", Object.class);
  }

  /**
   * Test the case when user gives a timeout longer than the max timeout of this client. In this
   * case, the CompletableFuture should finish within the max timeout.
   */
  @Test
  public void testServerNoResponse2() throws JsonRpcException {
    String id = getNextId();
    doNothing().when(transporter).send(any());

    CompletableFuture<Object> completableFuture =
        jsonRpcV1Client.call(id, "method", Object.class);
    try {
      completableFuture.get(MAX_TIMEOUT * 2, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException e) {
      fail(e.getMessage());
    } catch (ExecutionException e) {
      assertTrue(e.getCause() instanceof TimeoutException);
      return;
    }
    fail();
  }

  @Test
  public void testObjectArrayResult()
      throws JsonRpcException, TimeoutException, ExecutionException {
    String id = getNextId();
    String method = "echo";
    String[] expectedResult = {"string1", "string2", "string3"};

    JsonNode request = getRequestNode(id, method);
    setUpMockTransporter(
        expectedResult, null, id, request, randomDelayWithinTimeout());
    String[] result = syncCall(id, method, String[].class);

    assertArrayEquals(expectedResult, result);
  }

  @Test
  public void testVoidResult()
      throws JsonRpcException, JsonProcessingException, TimeoutException, ExecutionException {
    String id = getNextId();

    String method = "notify";
    String param1 = "some message";
    int param2 = 1;

    JsonNode request = getRequestNode(id, method, param1, param2);

    setUpMockTransporter(null, null, id, request, randomDelayWithinTimeout());

    assertNull(syncCall(id, method, Void.class, param1, param2));
  }

  @Test
  public void testErrorResult() throws JsonRpcException, TimeoutException {
    String id = getNextId();
    String method = "some_method";

    UUID unknownStudentId = UUID.randomUUID();
    JsonNode request = getRequestNode(id, method, unknownStudentId);

    String error = "unknown error";
    setUpMockTransporter(
        null, error, id, request, randomDelayWithinTimeout());

    try {
      syncCall(id, method, UUID.class, unknownStudentId);
    } catch (ExecutionException e) {
      assertTrue(e.getMessage().contains(error));
      return;
    }
    fail();
  }

  @Test
  public void testErrorReturnType() throws JsonRpcException, TimeoutException {
    String[] resultWithWrongType = new String[0];
    String id = getNextId();
    String method = "list_methods";

    JsonNode request = getRequestNode(id, method);

    setUpMockTransporter(
        resultWithWrongType, null, id, request, randomDelayWithinTimeout());

    try {
      syncCall(id, method, String.class);
    } catch (ExecutionException e) {
      assertTrue(e.getCause() instanceof JsonRpcException);
      assertTrue(e.getMessage().contains("Failed to convert result"));
      return;
    }
    fail();
  }

  @Test
  public void testNotify() throws JsonRpcException {
    String method = "notify";
    String param = "param";
    JsonNode request = getRequestNode(null, method, param);
    doNothing().when(transporter).send(request);
    jsonRpcV1Client.notify(method, param);
    verify(transporter, timeout(TIMEOUT).times(1)).send(eq(request));
  }

  @Test
  public void testInvalidResponse1() throws JsonRpcException {
    JsonNode invalidResponse = JsonUtil.toJsonNode("I am an invalid response");
    doAnswer(invocationOnMock -> {
      new Thread(() -> {
        try {
          jsonRpcV1Client.handleResponse(invalidResponse);
        } catch (JsonRpcException e) {
          e.printStackTrace();
        }
      }).start();
      return null;
    }).when(transporter).send(any());

    String id1 = (++counter).toString();
    JsonRpcConnectionClosedException exception = null;
    try {
      syncCall(id1, "method", String.class, "param");
    } catch (TimeoutException e) {
      fail(e.getMessage());
    } catch (ExecutionException e) {
      exception = (JsonRpcConnectionClosedException) e.getCause();
    }
    assertNotNull(exception);
    assertTrue(exception.getMessage().contains("Connection for this client is closed."));
    verify(transporter, times(1)).close();

    String id2 = (++counter).toString();
    try {
      jsonRpcV1Client.call(id2, "method", String.class, "param1");
    } catch (JsonRpcConnectionClosedException e) {
      return;
    }
    fail();
  }

  @Test
  public void testInvalidResponse2() throws JsonRpcException {
    JsonNode invalidResponse = JsonUtil.toJsonNode("I am an invalid response");
    String id1 = getNextId();
    CompletableFuture f1 = jsonRpcV1Client.call(id1, "method1", String.class);

    String id2 = getNextId();
    JsonNode requestNode2 = getRequestNode(id2, "method2");
    doAnswer(invocationOnMock -> {
      new Thread(() -> {
        try {
          jsonRpcV1Client.handleResponse(invalidResponse);
        } catch (JsonRpcException e) {
          e.printStackTrace();
        }
      }).start();
      return null;
    }).when(transporter).send(requestNode2);

    CompletableFuture f2 = jsonRpcV1Client.call(id2, "method2", String.class);

    verify(transporter, times(1)).send(requestNode2);
    verify(transporter, timeout(1000).times(1)).close();

    JsonRpcConnectionClosedException exception = null;
    try {
      f1.get(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException e) {
      fail();
    } catch (ExecutionException e) {
      exception = (JsonRpcConnectionClosedException) e.getCause();
    }

    assertNotNull(exception);
    assertTrue(exception.getMessage().contains("Connection for this client is closed."));

    exception = null;
    try {
      f2.get(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException e) {
      fail();
    } catch (ExecutionException e) {
      exception = (JsonRpcConnectionClosedException) e.getCause();
    }
    assertNotNull(exception);
    assertTrue(exception.getMessage().contains("Connection for this client is closed."));
  }

  @Test
  public void testResponseWithWrongId()
      throws JsonRpcException, ExecutionException, TimeoutException {
    JsonNode responseNode = getResponseNode(null, null, null);
    jsonRpcV1Client.handleResponse(responseNode);

    // Make sure after receiving null-id response, the client still functions well
    testServerResponseOnTime();
    testObjectArrayResult();

    responseNode = getResponseNode("unknownId", null, null);
    jsonRpcV1Client.handleResponse(responseNode);

    // Make sure after receiving wrong-id response, the client still functions well
    testServerResponseOnTime();
    testObjectArrayResult();
  }

  @Test(expected = JsonRpcTransportException.class)
  public void testTransporterException() throws JsonRpcException {
    String id = getNextId();

    doThrow(new JsonRpcTransportException("Test exception")).when(transporter).send(any());
    jsonRpcV1Client.call(id, "method", Object.class);
  }

  @Test
  public void testDuplicateId() throws JsonRpcException {
    String id = "randomId";
    String method = "method";

    doNothing().when(transporter).send(any());
    jsonRpcV1Client.call(id, method, Object.class);

    try {
      jsonRpcV1Client.call(id, method, Object.class);
    } catch (JsonRpcDuplicateIdException e) {
      return;
    }
    fail();
  }

  private void setUpMockTransporter(
      Object result, String error, String id, JsonNode request, int delay
  ) throws JsonRpcTransportException {
    JsonNode expectedResponse = getResponseNode(id, result, error);

    doAnswer(invocationOnMock -> {
      new Thread(() -> {
        try {
          TimeUnit.SECONDS.sleep(delay);
          jsonRpcV1Client.handleResponse(expectedResponse);
        } catch (InterruptedException | JsonRpcException e) {
          fail(e.getMessage());
        }
      }).start();
      return null;
    }).when(transporter).send(request);
  }

  private int randomDelayWithinTimeout() {
    return ThreadLocalRandom.current().nextInt(0, TIMEOUT);
  }

  private <T> T syncCall(String id, String method, Class<T> returnType, Object... params)
      throws JsonRpcException, TimeoutException, ExecutionException {
    CompletableFuture<T> completableFuture =
        jsonRpcV1Client.call(id, method, returnType, params);
    T result = null;
    try {
      result = completableFuture.get(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      fail(e.getMessage());
    }
    return result;
  }

  private String getNextId() {
    return (++counter).toString();
  }
}
