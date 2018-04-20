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
import static junit.framework.TestCase.assertNull;

import com.vmware.ovsdb.service.impl.OvsdbActiveConnectionConnectorImpl;
import com.vmware.ovsdb.util.PropertyManager;
import com.vmware.ovsdb.utils.PassiveOvsdbServerEmulator;
import com.vmware.ovsdb.utils.SslUtil.SelfSignedSslContextPair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OvsdbActiveConnectionConnectorTest {

  private static final String HOST = "127.0.0.1";

  private static final int PORT = 6641; // Use port 6641 for testing purpose

  private static final int TEST_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

  private final OvsdbActiveConnectionConnector activeConnectionConnector =
      new OvsdbActiveConnectionConnectorImpl(executorService);

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testTcpConnection() throws InterruptedException {
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

    for (int port : ports) {
      if (sslCtxPair == null) {
        activeConnectionConnector.connect(HOST, port).join();
      } else {
        activeConnectionConnector.connectWithSsl(HOST, port, sslCtxPair.getClientSslCtx()).join();
      }
    }

    passiveOvsdbServers.forEach(passiveOvsdbServer -> passiveOvsdbServer.stopListening().join());
  }

  private void testWriteInvalidJson(SelfSignedSslContextPair sslCtxPair)
      throws InterruptedException {
    final PassiveOvsdbServerEmulator passiveOvsdbServer = new PassiveOvsdbServerEmulator(PORT);
    if (sslCtxPair == null) {
      passiveOvsdbServer.startListening().join();
      activeConnectionConnector.connect(HOST, PORT).join();
    } else {
      passiveOvsdbServer.startListeningWithSsl(sslCtxPair.getServerSslCtx()).join();
      activeConnectionConnector.connectWithSsl(HOST, PORT, sslCtxPair.getClientSslCtx()).join();
    }

    while (passiveOvsdbServer.getConnectionInfo() == null) {
      TimeUnit.MILLISECONDS.sleep(100);
    }

    // Write an invalid Json to the channel. The ExceptionHandler should
    // close the channel
    passiveOvsdbServer.write("}\"msg\":\"IAmInvalidJson\"{");

    while (passiveOvsdbServer.getConnectionInfo() != null) {
      TimeUnit.MILLISECONDS.sleep(100);
    }

    passiveOvsdbServer.stopListening().join();
  }

  private void testChannelTimeout(SelfSignedSslContextPair sslCtxPair) throws InterruptedException {
    final PassiveOvsdbServerEmulator passiveOvsdbServer = new PassiveOvsdbServerEmulator(PORT);
    if (sslCtxPair == null) {
      passiveOvsdbServer.startListening().join();
      activeConnectionConnector.connect(HOST, PORT).join();
    } else {
      passiveOvsdbServer.startListeningWithSsl(sslCtxPair.getServerSslCtx()).join();
      activeConnectionConnector.connectWithSsl(HOST, PORT,
          sslCtxPair.getClientSslCtx()).join();
    }

    long readIdleTimeout = PropertyManager.getLongProperty("channel.read.idle.timeout.sec", 5);
    int readIdleMax = PropertyManager.getIntProperty("channel.read.idle.max", 3);
    // Wait until the ovsdb manager closes the channel
    TimeUnit.SECONDS.sleep(readIdleTimeout * readIdleMax + 2);
    assertNull(passiveOvsdbServer.getConnectionInfo());

    passiveOvsdbServer.stopListening().join();
  }
}
