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
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.service.impl.OvsdbPassiveConnectionListenerImpl;
import com.vmware.ovsdb.util.PropertyManager;
import com.vmware.ovsdb.utils.ActiveOvsdbServerEmulator;
import com.vmware.ovsdb.utils.SslUtil.SelfSignedSslContextPair;
import io.netty.handler.ssl.SslContext;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class OvsdbPassiveConnectionListenerTest {

  private static final String HOST = "127.0.0.1";

  private static final int PORT = 6641; // Use port 6641 for testing purpose

  private static final int TEST_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds

  private static final int VERIFY_TIMEOUT_MILLIS = 5000; // 5 seconds

  private static final ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

  private final OvsdbPassiveConnectionListener passiveListener =
      new OvsdbPassiveConnectionListenerImpl(executorService);

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testTcpConnection() throws Exception {
    passiveListener.startListening(PORT, mockConnectionCallback).join();
    testConnectionBasic(null);
    testConnectThenDisconnect(null);
    testWriteInvalidJson(null);
    testChannelTimeout(null);
    passiveListener.stopListening(PORT).join();
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testSslConnection() throws Exception {
    SelfSignedSslContextPair sslContextPair = newSelfSignedSslContextPair();
    // In passive connection test, the controller is the server and the ovsdb-server is the client
    SslContext serverSslCtx = sslContextPair.getServerSslCtx();
    SslContext clientSslCtx = sslContextPair.getClientSslCtx();
    passiveListener.startListeningWithSsl(PORT, serverSslCtx, mockConnectionCallback).join();
    testConnectionBasic(clientSslCtx);
    testConnectThenDisconnect(clientSslCtx);
    testWriteInvalidJson(clientSslCtx);
    testChannelTimeout(clientSslCtx);
    passiveListener.stopListening(PORT).join();
  }

  private void testConnectionBasic(SslContext sslCtx) {
    reset(mockConnectionCallback);
    final int expectedConnectionCnt = 10;
    List<ActiveOvsdbServerEmulator> activeOvsdbServers =
        getActiveOvsdbServers(expectedConnectionCnt);

    activeOvsdbServers.forEach(activeOvsdbServer -> {
      if (sslCtx == null) {
        activeOvsdbServer.connect().join();
      } else {
        activeOvsdbServer.connectWithSsl(sslCtx).join();
      }
    });

    activeOvsdbServers.forEach(ActiveOvsdbServerEmulator::disconnect);

    verifyConnectDisconnectCnt(expectedConnectionCnt);
  }

  private void testConnectThenDisconnect(SslContext sslCtx) {
    reset(mockConnectionCallback);
    final int expectedConnectionCnt = 10;
    getActiveOvsdbServers(expectedConnectionCnt).forEach(activeOvsdbServer -> {
      try {
        if (sslCtx == null) {
          activeOvsdbServer.connect().join();
        } else {
          activeOvsdbServer.connectWithSsl(sslCtx).join();
        }
        TimeUnit.MILLISECONDS.sleep(100);
        activeOvsdbServer.disconnect().join();
      } catch (InterruptedException e) {
        fail(e.getMessage());
      }
    });

    verifyConnectDisconnectCnt(expectedConnectionCnt);
  }

  private void testWriteInvalidJson(SslContext sslCtx) {
    reset(mockConnectionCallback);
    final int expectedConnectionCnt = 1;
    final ActiveOvsdbServerEmulator activeOvsdbServerEmulator =
        new ActiveOvsdbServerEmulator(HOST, PORT);
    if (sslCtx == null) {
      activeOvsdbServerEmulator.connect().join();
    } else {
      activeOvsdbServerEmulator.connectWithSsl(sslCtx).join();
    }

    // Write an invalid Json to the channel. The ExceptionHandler should
    // close the channel
    activeOvsdbServerEmulator.write("}\"msg\":\"IAmInvalidJson\"{");

    verifyConnectDisconnectCnt(expectedConnectionCnt);
  }

  private void testChannelTimeout(SslContext sslCtx) throws Exception {
    reset(mockConnectionCallback);
    final int expectedConnectionCnt = 1;
    final ActiveOvsdbServerEmulator activeOvsdbServerEmulator =
        new ActiveOvsdbServerEmulator(HOST, PORT);
    if (sslCtx == null) {
      activeOvsdbServerEmulator.connect().join();
    } else {
      activeOvsdbServerEmulator.connectWithSsl(sslCtx).join();
    }

    long readIdleTimeout = PropertyManager.getLongProperty("channel.read.idle.timeout.sec", 5);
    int readIdleMax = PropertyManager.getIntProperty("channel.read.idle.max", 3);
    // Wait until the ovsdb manager closes the channel
    TimeUnit.SECONDS.sleep(readIdleTimeout * readIdleMax + 2);

    verifyConnectDisconnectCnt(expectedConnectionCnt);
  }

  private List<ActiveOvsdbServerEmulator> getActiveOvsdbServers(int count) {
    return IntStream.range(0, count).mapToObj(i -> new ActiveOvsdbServerEmulator(HOST, PORT))
        .collect(Collectors.toList());
  }

  private void verifyConnectDisconnectCnt(int expectedCount) {
    verify(mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedCount)
    ).connected(any());

    verify(mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedCount)
    ).disconnected(any());
  }
}
