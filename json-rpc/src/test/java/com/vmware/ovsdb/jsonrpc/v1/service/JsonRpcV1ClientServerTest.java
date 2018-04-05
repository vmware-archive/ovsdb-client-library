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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;
import com.vmware.ovsdb.jsonrpc.v1.service.domain.SillyCalculator;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ClientImpl;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ServerImpl;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.Test;

public class JsonRpcV1ClientServerTest {

  private static final int DELAY_RANGE_BEGIN = 100;
  private static final int DELAY_RANGE_END = 500;
  private final SillyCalculator sillyCalculator = new SillyCalculator();
  private final Random rand = new Random();
  private final String[] methods = {"add", "sub", "mul"};
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  /**
   * Multiple clients send requests to the server concurrently. Each client sends requests
   * sequentially.
   */
  @Test
  public void testSequentialRequests() throws InterruptedException {
    testMultiClients(10, 10, false);
  }

  /**
   * Multiple clients send requests to the server concurrently. Each client sends requests
   * concurrently.
   */
  @Test
  public void testMultiConcurrentClients() throws InterruptedException {
    testMultiClients(10, 10, true);
  }

  private void sendRequest(JsonRpcV1Client client, String id,
      ConcurrentLinkedQueue<String> errorMsgs) {
    String method = methods[rand.nextInt(methods.length)];
    int a = rand.nextInt();
    int b = rand.nextInt();
    try {
      CompletableFuture<Integer> f = client.call(id, method, Integer.class, a, b);
      int result = f.get();
      int expectedResult = getExpectedResult(method, a, b);
      if (result != expectedResult) {
        errorMsgs.add("Result of " + method + "(" + a + "," + b + ")"
            + " is incorrect. Expect: " + expectedResult + ". Got: "
            + result);
      }
    } catch (JsonRpcException | InterruptedException | ExecutionException e) {
      errorMsgs.add(e.getMessage());
    }
  }


  private void testMultiClients(int numClient, int numRequestsPerClient,
      boolean concurrentRequests)
      throws InterruptedException {

    List<JsonRpcV1Client> clients = new ArrayList<>();
    JsonRpcTransporter serverTransporter = new ServerTransporter(clients,
        id -> Integer.valueOf(id) / numRequestsPerClient);
    JsonRpcV1Server server = new JsonRpcV1ServerImpl(serverTransporter, sillyCalculator);

    Thread[] clientThreads = new Thread[numClient];
    ConcurrentLinkedQueue<String> errorMsgs = new ConcurrentLinkedQueue<>();

    for (int i = 0; i < numClient; i++) {
      JsonRpcTransporter clientTransporter = new ClientTransporter(server);
      JsonRpcV1Client client = new JsonRpcV1ClientImpl(clientTransporter, scheduler);
      clients.add(client);

      final int ID_BASE = numRequestsPerClient * i;
      clientThreads[i] = new Thread(() -> {
        // For e.g. numRequestsPerClient is 4
        // Then client 0 sends request with id 0,1,2,3
        // client 1 sends request with id 4,5,6,7, etc

        Thread[] requestsThreads = new Thread[concurrentRequests ? numRequestsPerClient
            : 0];
        for (int j = 0; j < numRequestsPerClient; j++) {
          String id = String.valueOf(ID_BASE + j);
          if (concurrentRequests) {
            requestsThreads[j] = new Thread(() -> sendRequest(client, id, errorMsgs));
            requestsThreads[j].start();
          } else {
            sendRequest(client, String.valueOf(id), errorMsgs);
          }
        }
        for (Thread requestsThread : requestsThreads) {
          try {
            requestsThread.join();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
    }

    for (int i = 0; i < numClient; i++) {
      clientThreads[i].start();
    }
    for (int i = 0; i < numClient; i++) {
      clientThreads[i].join();
    }
    assertEquals(0, errorMsgs.size());
  }

  private int getExpectedResult(String method, int a, int b) {
    switch (method) {
      case "add":
        return a + b;
      case "sub":
        return a - b;
      case "mul":
        return a * b;
    }
    return 0;
  }

  private int getRandomDelay() {
    return rand.nextInt(DELAY_RANGE_END) + DELAY_RANGE_BEGIN;
  }

  /**
   * This is the transporter for the client. When send() is called, it creates a new thread that
   * calls server.handlerRequest(). A random delay is inserted before that.
   */
  private class ClientTransporter implements JsonRpcTransporter {

    private final JsonRpcV1Server server;

    ClientTransporter(JsonRpcV1Server server) {
      this.server = server;
    }

    @Override
    public void send(JsonNode data) throws JsonRpcTransportException {
      new Thread(() -> {
        try {
          // Insert a random delay before the server processes the request
          TimeUnit.MILLISECONDS.sleep(getRandomDelay());
          server.handleRequest(data);
        } catch (JsonRpcException | InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    }

    @Override
    public void close() {

    }
  }

  /**
   * This is the transporter for the server. It has all the response queues of the clients. When
   * send() is called, it creates a new thread to call handleResponse() on the client. A random
   * delay is inserted before that.
   */
  private class ServerTransporter implements JsonRpcTransporter {

    private final List<JsonRpcV1Client> clients;

    private final Function<String, Integer> getClientIndexFromId;

    /**
     * @param clients the clients that connected to the server
     * @param getClientIndexFromId a function used to get the client index through a request id
     */
    ServerTransporter(List<JsonRpcV1Client> clients,
        Function<String, Integer> getClientIndexFromId) {
      this.clients = clients;
      this.getClientIndexFromId = getClientIndexFromId;
    }

    @Override
    public void send(JsonNode data) throws JsonRpcTransportException {
      new Thread(() -> {
        try {
          TimeUnit.MILLISECONDS.sleep(getRandomDelay());
          int clientIndex = getClientIndexFromId.apply(data.get("id").asText());
          clients.get(clientIndex).handleResponse(data);
        } catch (InterruptedException | JsonRpcException e) {
          fail(e.getMessage());
        }
      }).start();
    }

    @Override
    public void close() {

    }
  }

}
