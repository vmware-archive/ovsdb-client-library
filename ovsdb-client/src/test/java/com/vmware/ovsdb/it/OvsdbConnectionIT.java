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

package com.vmware.ovsdb.it;

import static com.vmware.ovsdb.testutils.DockerUtil.killContainers;
import static com.vmware.ovsdb.testutils.DockerUtil.startContainerWithPortBinding;
import static com.vmware.ovsdb.testutils.DockerUtil.startContainers;
import static com.vmware.ovsdb.testutils.SslUtil.getPrivateKeyPem;
import static com.vmware.ovsdb.testutils.SslUtil.getX509CertPem;
import static com.vmware.ovsdb.testutils.TestConstants.HOST_IP;
import static com.vmware.ovsdb.testutils.TestConstants.LOCAL_HOST;
import static com.vmware.ovsdb.testutils.TestConstants.OVSDB_PORT;
import static com.vmware.ovsdb.testutils.TestConstants.OVS_DOCKER_IMAGE;
import static com.vmware.ovsdb.testutils.TestConstants.TEST_TIMEOUT_MILLIS;
import static com.vmware.ovsdb.testutils.TestConstants.VERIFY_TIMEOUT_MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.spotify.docker.client.exceptions.DockerException;
import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.service.OvsdbActiveConnectionConnector;
import com.vmware.ovsdb.service.OvsdbPassiveConnectionListener;
import com.vmware.ovsdb.service.impl.OvsdbActiveConnectionConnectorImpl;
import com.vmware.ovsdb.service.impl.OvsdbPassiveConnectionListenerImpl;
import com.vmware.ovsdb.testutils.SslUtil;
import com.vmware.ovsdb.testutils.SslUtil.SelfSignedSslContextPair;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import javax.net.ssl.SSLException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class OvsdbConnectionIT {

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

  private final OvsdbPassiveConnectionListener passiveConnectionListener =
      new OvsdbPassiveConnectionListenerImpl(executorService);

  private final OvsdbActiveConnectionConnector activeConnectionConnector =
      new OvsdbActiveConnectionConnectorImpl(executorService);

  /**
   * The controller listens on the port and waits for the ovsdb-server to connect.
   * 1. Start listener on OVSDB_PORT.
   * 2. Start n dockers of ovsdb-server that will actively connect to the controller.
   * 3. Verify that connected callback is called n times.
   * 4. Stop all dockers.
   * 5. Verify that the disconnected callback is called n times.
   */
  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testPassiveTcpConnection() throws Exception {
    final int expectedConnectionCnt = 10;
    ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);
    passiveConnectionListener.startListening(OVSDB_PORT, mockConnectionCallback).join();

    String cmd = "ovsdb-server --log-file --remote=tcp:" + HOST_IP + ":" + OVSDB_PORT;
    List<String> dockerIds = startContainers(expectedConnectionCnt, OVS_DOCKER_IMAGE, cmd);

    verify(
        mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
    ).connected(ArgumentMatchers.any());

    killContainers(dockerIds);

    verify(
        mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
    ).disconnected(ArgumentMatchers.any());

    passiveConnectionListener.stopListening(OVSDB_PORT).join();
  }

  /**
   * Similar to {@link OvsdbConnectionIT#testPassiveTcpConnection()} but with SSL.
   */
  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testPassiveSslConnection() throws Exception {
    final int expectedConnectionCnt = 10;
    SelfSignedSslContextPair sslContextPair = SslUtil.newSelfSignedSslContextPair();
    SslContext serverSslCtx = sslContextPair.getServerSslCtx();
    SelfSignedCertificate clientCert = sslContextPair.getClientCert();
    SelfSignedCertificate serverCert = sslContextPair.getServerCert();

    String clientCertPem = getX509CertPem(clientCert.cert());
    String clientPrivateKeyPem = getPrivateKeyPem(clientCert.key());
    // Because server is self-signed. The CA cert is just the server's cert.
    String caCertPem = getX509CertPem(serverCert.cert());

    ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);
    passiveConnectionListener
        .startListeningWithSsl(OVSDB_PORT, serverSslCtx, mockConnectionCallback).join();

    // All ovsdb servers use the same private key and certificate for testing purpose
    // DON'T DO THIS IN ANY PRODUCTION
    String cmd = "echo '" + clientPrivateKeyPem + "' > privkey.pem && "
        + "echo '" + clientCertPem + "' > cert.pem && "
        + "echo '" + caCertPem + "' > cacert.pem && "
        + "ovsdb-server --log-file --remote=ssl:" + HOST_IP + ":" + OVSDB_PORT
        + " --private-key=privkey.pem --certificate=cert.pem --ca-cert=cacert.pem";
    List<String> dockerIds = startContainers(expectedConnectionCnt, OVS_DOCKER_IMAGE, cmd);

    verify(
        mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
    ).connected(any());

    killContainers(dockerIds);

    verify(
        mockConnectionCallback,
        timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
    ).disconnected(any());

    passiveConnectionListener.stopListening(OVSDB_PORT).join();
  }

  /**
   * The controller listens on the port and waits for the ovsdb-server to connect.
   * 1. Start ovsdb-servers that listens on port 6640, 6641, ...
   * 2. Connect to ovsdb-servers started in step 1
   * 3. Verify that connected callback is called n times.
   * 4. Stop all ovsdb-servers
   * 5. Verify that the disconnected callback is called n times.
   */
  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testActiveTcpConnection() throws DockerException, InterruptedException {
    final int expectedConnectionCnt = 10;
    final int startPort = OVSDB_PORT;
    List<String> containerIds = new ArrayList<>();
    for (int port = startPort; port < startPort + expectedConnectionCnt; port++) {
      String cmd = "ovsdb-server --log-file --remote=ptcp:" + port;
      String id = startContainerWithPortBinding(OVS_DOCKER_IMAGE, String.valueOf(port), cmd);
      containerIds.add(id);
    }

    for (int port = startPort; port < startPort + expectedConnectionCnt; port++) {
      activeConnectionConnector.connect(LOCAL_HOST, port).join();
    }

    killContainers(containerIds);
  }

  /**
   * Similar to {@link OvsdbConnectionIT#testActiveTcpConnection()} but with SSL.
   */
  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testActiveSslConnection()
      throws DockerException, InterruptedException, CertificateException, SSLException {
    final int expectedConnectionCnt = 10;
    final int startPort = OVSDB_PORT;
    List<String> containerIds = new ArrayList<>();

    SelfSignedSslContextPair sslContextPair = SslUtil.newSelfSignedSslContextPair();
    SslContext clientSslCtx = sslContextPair.getClientSslCtx();
    SelfSignedCertificate clientCert = sslContextPair.getClientCert();
    SelfSignedCertificate serverCert = sslContextPair.getServerCert();

    String serverPrivateKeyPem = getPrivateKeyPem(serverCert.key());
    String serverCertPem = getX509CertPem(serverCert.cert());
    // Because client is self-signed. The CA cert is just the client's cert.
    String caCertPem = getX509CertPem(clientCert.cert());

    // All controllers use the same private key and certificate for testing purpose
    // DON'T DO THIS IN ANY PRODUCTION
    for (int port = startPort; port < startPort + expectedConnectionCnt; port++) {
      String cmd = "echo '" + serverPrivateKeyPem + "' > privkey.pem && "
          + "echo '" + serverCertPem + "' > cert.pem && "
          + "echo '" + caCertPem + "' > cacert.pem && "
          + "ovsdb-server --log-file --remote=pssl:" + port
          + " --private-key=privkey.pem --certificate=cert.pem --ca-cert=cacert.pem";
      String id = startContainerWithPortBinding(OVS_DOCKER_IMAGE, String.valueOf(port), cmd);
      containerIds.add(id);
    }

    for (int port = startPort; port < startPort + expectedConnectionCnt; port++) {
      activeConnectionConnector.connectWithSsl(LOCAL_HOST, port, clientSslCtx).join();
    }

    killContainers(containerIds);
  }

}
