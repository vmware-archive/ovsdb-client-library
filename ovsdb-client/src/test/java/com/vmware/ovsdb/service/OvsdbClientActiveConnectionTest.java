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

import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.service.impl.OvsdbActiveConnectionConnectorImpl;
import com.vmware.ovsdb.utils.PassiveOvsdbServerEmulator;
import io.netty.handler.ssl.SslContext;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

public class OvsdbClientActiveConnectionTest extends OvsdbClientTest {

  private static final OvsdbActiveConnectionConnector activeConnector
      = new OvsdbActiveConnectionConnectorImpl(executorService);

  private static final PassiveOvsdbServerEmulator passiveOvsdbServer =
      new PassiveOvsdbServerEmulator(PORT);

  public OvsdbClientActiveConnectionTest() {
    super(passiveOvsdbServer);
  }

  @After
  public void tearDown() {
    passiveOvsdbServer.stopListening().join();
  }

  @Override
  void setUp(boolean withSsl) {
    if (!withSsl) {
      passiveOvsdbServer.startListening().join();
      activeConnector.connect(HOST, PORT, connectionCallback);
    } else {
      // In passive connection test, the controller is the server and the ovsdb-server is the client
      SslContext serverSslCtx = sslContextPair.getServerSslCtx();
      SslContext clientSslCtx = sslContextPair.getClientSslCtx();
      passiveOvsdbServer.startListeningWithSsl(serverSslCtx);
      activeConnector.connectWithSsl(HOST, PORT, clientSslCtx, connectionCallback);
    }
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testTcpConnection() throws OvsdbClientException, IOException {
    super.testTcpConnection();
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testSslConnection() throws OvsdbClientException, IOException {
    super.testSslConnection();
  }
}
