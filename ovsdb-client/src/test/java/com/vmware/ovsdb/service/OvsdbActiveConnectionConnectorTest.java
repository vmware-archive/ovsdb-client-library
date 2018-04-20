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

package com.vmware.ovsdb.service;

import static com.vmware.ovsdb.utils.SslUtil.newSelfSignedSslContextPair;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.service.impl.OvsdbActiveConnectionConnectorImpl;
import com.vmware.ovsdb.util.PropertyManager;
import com.vmware.ovsdb.utils.PassiveOvsdbServerEmulator;
import com.vmware.ovsdb.utils.SslUtil.SelfSignedSslContextPair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OvsdbActiveConnectionConnectorTest {

  private static final String HOST = "127.0.0.1";

  private static final int PORT = 6641; // Use port 6641 for testing purpose

  private static final int TEST_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds

  private static final int VERIFY_TIMEOUT_MILLIS = 5000; // 5 seconds

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

  private final OvsdbActiveConnectionConnector activeConnectionConnector =
      new OvsdbActiveConnectionConnectorImpl(executorService);

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testTcpConnection() {
    testConnectionBasic(null);
    testWriteInvalidJson(null);
    testChannelTimeout(null);
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testSslConnection() throws Exception {
    SelfSignedSslContextPair sslContextPair = newSelfSignedSslContextPair();
    // In active connection test, the controller is the client and the ovsdb-server is the server
    testConnectionBasic(sslContextPair);
    testWriteInvalidJson(sslContextPair);
    testChannelTimeout(sslContextPair);
  }

  private void testConnectionBasic(SelfSignedSslContextPair sslCtxPair) {
    final int ovsdbServerCnt = 10;
    final List<Integer> ports = IntStream.range(PORT, PORT + ovsdbServerCnt)
        .boxed().collect(Collectors.toList());

    final List<PassiveOvsdbServerEmulator> passiveOvsdbServers = new ArrayList<>();

    for (int port : ports) {
      PassiveOvsdbServerEmulator passiveOvsdbServer = new PassiveOvsdbServerEmulator(port);
      passiveOvsdbServers.add(passiveOvsdbServer);
      if (sslCtxPair == null) {
        passiveOvsdbServer.startListening().join();
      } else {
        passiveOvsdbServer.startListeningWithSsl(sslCtxPair.getServerSslCtx()).join();
      }
    }

    final List<ConnectionCallback> connectionCallbacks = new ArrayList<>();

    for (int port : ports) {
      ConnectionCallback connectionCallback = mock(ConnectionCallback.class);
      connectionCallbacks.add(connectionCallback);
      if (sslCtxPair == null) {
        activeConnectionConnector.connect(HOST, port, connectionCallback);
      } else {
        activeConnectionConnector.connectWithSsl(HOST, port,
            sslCtxPair.getClientSslCtx(), connectionCallback);
      }
    }

    for (ConnectionCallback connectionCallback : connectionCallbacks) {
      verify(connectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)).connected(any());
    }

    passiveOvsdbServers.forEach(PassiveOvsdbServerEmulator::disconnect);

    for (ConnectionCallback connectionCallback : connectionCallbacks) {
      verify(connectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)).disconnected(any());
    }

    passiveOvsdbServers.forEach(passiveOvsdbServer -> passiveOvsdbServer.stopListening().join());
  }

  private void testWriteInvalidJson(SelfSignedSslContextPair sslCtxPair) {
    final PassiveOvsdbServerEmulator passiveOvsdbServer = new PassiveOvsdbServerEmulator(PORT);
    ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);
    if (sslCtxPair == null) {
      passiveOvsdbServer.startListening().join();
      activeConnectionConnector.connect(HOST, PORT, mockConnectionCallback);
    } else {
      passiveOvsdbServer.startListeningWithSsl(sslCtxPair.getServerSslCtx()).join();
      activeConnectionConnector.connectWithSsl(HOST, PORT,
          sslCtxPair.getClientSslCtx(), mockConnectionCallback);
    }

    verify(mockConnectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)).connected(any());

    // Write an invalid Json to the channel. The ExceptionHandler should
    // close the channel
    passiveOvsdbServer.write("}\"msg\":\"IAmInvalidJson\"{");
    //passiveOvsdbServer.write("{\"method\":\"echo\",\"params\":[],\"id\":\"1111\"");

    verify(mockConnectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)).disconnected(any());

    passiveOvsdbServer.stopListening().join();
  }

  private void testChannelTimeout(SelfSignedSslContextPair sslCtxPair) {
    final PassiveOvsdbServerEmulator passiveOvsdbServer = new PassiveOvsdbServerEmulator(PORT);
    ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);
    if (sslCtxPair == null) {
      passiveOvsdbServer.startListening().join();
      activeConnectionConnector.connect(HOST, PORT, mockConnectionCallback);
    } else {
      passiveOvsdbServer.startListeningWithSsl(sslCtxPair.getServerSslCtx()).join();
      activeConnectionConnector.connectWithSsl(HOST, PORT,
          sslCtxPair.getClientSslCtx(), mockConnectionCallback);
    }

    verify(mockConnectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)).connected(any());

    long readIdleTimeout = PropertyManager.getLongProperty("channel.read.idle.timeout.sec", 5);
    int readIdleMax = PropertyManager.getIntProperty("channel.read.idle.max", 3);
    // Wait until the ovsdb manager closes the channel
    verify(mockConnectionCallback, timeout((readIdleTimeout * readIdleMax + 2) * 1000))
        .disconnected(any());

    passiveOvsdbServer.stopListening().join();
  }
}
