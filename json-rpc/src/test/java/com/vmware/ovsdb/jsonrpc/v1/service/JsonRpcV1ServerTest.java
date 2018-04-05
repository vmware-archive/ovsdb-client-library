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
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.annotation.JsonRpcServiceMethod;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcConnectionClosedException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;
import com.vmware.ovsdb.jsonrpc.v1.service.domain.SillyCalculator;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ServerImpl;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class JsonRpcV1ServerTest {

  private static final JsonRpcTransporter mockTransporter = mock(JsonRpcTransporter.class);

  private static final SillyCalculator sillyCalculator = new SillyCalculator();
  @Rule
  public TestName testName = new TestName();
  private JsonRpcV1Server jsonRpcServer;

  @Before
  public void setUp() {
    reset(mockTransporter);
    jsonRpcServer = new JsonRpcV1ServerImpl(mockTransporter, sillyCalculator);
  }

  @Test
  public void testBasic1() throws JsonRpcException {
    String id = testName.getMethodName();

    jsonRpcServer.handleRequest(getRequestNode(id, "list_methods"));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, sillyCalculator.listMethods(), null)
    );

  }

  @Test
  public void testBasic2() throws JsonRpcException {
    String id = testName.getMethodName();
    int param1 = 35, param2 = 42;

    jsonRpcServer.handleRequest(getRequestNode(id, "add", param1, param2));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, param1 + param2, null)
    );

    jsonRpcServer.handleRequest(getRequestNode(id, "sub", param1, param2));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, param1 - param2, null)
    );

    jsonRpcServer.handleRequest(getRequestNode(id, "mul", param1, param2));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, param1 * param2, null)
    );

    jsonRpcServer.handleRequest(getRequestNode(id, "echo", param1, param2));
    verify(mockTransporter, times(1)).send(
        getResponseNode(id, new Object[] {param1, param2}, null)
    );

    jsonRpcServer.handleRequest(
        getRequestNode(id, "print", "msg", 1));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null, null)
    );
  }

  @Test
  public void testNonExistingMethod() throws JsonRpcException {
    String id = testName.getMethodName();
    jsonRpcServer.handleRequest(getRequestNode(id, "clearStudents"));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null, "unknown method clearStudents")
    );
  }

  @Test
  public void testMethodError() throws JsonRpcException {
    String id = testName.getMethodName();
    String errorMsg = "Error!";
    jsonRpcServer.handleRequest(
        getRequestNode(id, "error_method", errorMsg));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null, errorMsg)
    );
  }

  @Test
  public void testParamsMismatch() throws JsonRpcException {
    String id = testName.getMethodName();

    jsonRpcServer.handleRequest(
        getRequestNode(id, "add", 1, 2, 3));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null, "Parameters number doesn't match. Expected: 2. Got: 3")
    );

    String wrongParam = "I am a wrong param";
    jsonRpcServer.handleRequest(getRequestNode(id, "sub", wrongParam, wrongParam));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null,
            "Failed to convert param \"I am a wrong param\" to type int")
    );
  }

  @Test
  public void testVarArgs() throws JsonRpcException {
    String id = testName.getMethodName();
    String param1 = "param2";
    Integer param2 = 2;
    List<Boolean> param3 = Arrays.asList(true, false);

    jsonRpcServer.handleRequest(
        getRequestNode(id, "echo", param1, param2, param3));

    verify(mockTransporter, times(1)).send(
        getResponseNode(id, new Object[] {param1, param2, param3}, null)
    );
  }

  @Test
  public void testInvalidRequest() throws JsonRpcException {
    JsonNode jsonNode = JsonUtil.toJsonNode("I am an invalid response");

    jsonRpcServer.handleRequest(jsonNode);

    verify(mockTransporter, times(1)).close();

    try {
      jsonRpcServer.handleRequest(jsonNode);
    } catch (JsonRpcConnectionClosedException e) {
      assertTrue(e.getMessage().contains("Connection for this server is closed."));
      return;
    }
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDuplicateMethodName() {
    class ClassWithDuplicateMethodName {

      @JsonRpcServiceMethod(value = "method")
      public void method1() {

      }

      @JsonRpcServiceMethod(value = "method")
      public void method2() {

      }
    }
    jsonRpcServer = new JsonRpcV1ServerImpl(mockTransporter,
        new ClassWithDuplicateMethodName());
  }

  @Test(expected = JsonRpcTransportException.class)
  public void testTransporterException() throws JsonRpcException {
    JsonNode requestNode = getRequestNode(testName.getMethodName(), "method");
    doThrow(new JsonRpcTransportException("Test exception")).when(mockTransporter).send(any());
    jsonRpcServer.handleRequest(requestNode);
  }

  @Test
  public void testInvalidVarArgs() throws JsonRpcException {
    String id = testName.getMethodName();
    int param1 = 42;
    String param2 = "I am an invalid param";
    jsonRpcServer.handleRequest(
        getRequestNode(id, "sum", param1, param2));

    String expectedError = "Failed to convert param \"I am an invalid param\" to type "
        + "class java.lang.Integer";
    verify(mockTransporter, times(1)).send(
        getResponseNode(id, null, expectedError)
    );
  }
}
