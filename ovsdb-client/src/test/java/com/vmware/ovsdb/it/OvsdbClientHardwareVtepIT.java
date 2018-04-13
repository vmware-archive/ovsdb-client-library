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

import static com.vmware.ovsdb.testutils.DockerUtil.execCommand;
import static com.vmware.ovsdb.testutils.DockerUtil.killContainers;
import static com.vmware.ovsdb.testutils.DockerUtil.startContainers;
import static com.vmware.ovsdb.testutils.HardwareGatewayConstants.HARDWARE_VTEP;
import static com.vmware.ovsdb.testutils.HardwareGatewayConstants.LOGICAL_SWITCH;
import static com.vmware.ovsdb.testutils.HardwareGatewayConstants.PHYSICAL_LOCATOR;
import static com.vmware.ovsdb.testutils.HardwareGatewayConstants.UCAST_MACS_REMOTE;
import static com.vmware.ovsdb.testutils.SslUtil.getPrivateKeyPem;
import static com.vmware.ovsdb.testutils.SslUtil.getX509CertPem;
import static com.vmware.ovsdb.testutils.TestConstants.HOST_IP;
import static com.vmware.ovsdb.testutils.TestConstants.OVSDB_PORT;
import static com.vmware.ovsdb.testutils.TestConstants.OVS_DOCKER_IMAGE;
import static com.vmware.ovsdb.testutils.TestConstants.TEST_TIMEOUT_MILLIS;
import static com.vmware.ovsdb.testutils.TestConstants.VERIFY_TIMEOUT_MILLIS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.exceptions.DockerException;
import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.protocol.operation.Insert;
import com.vmware.ovsdb.protocol.operation.Operation;
import com.vmware.ovsdb.protocol.operation.Select;
import com.vmware.ovsdb.protocol.operation.notation.Function;
import com.vmware.ovsdb.protocol.operation.notation.Row;
import com.vmware.ovsdb.protocol.operation.notation.Uuid;
import com.vmware.ovsdb.protocol.operation.result.InsertResult;
import com.vmware.ovsdb.protocol.operation.result.OperationResult;
import com.vmware.ovsdb.protocol.operation.result.SelectResult;
import com.vmware.ovsdb.protocol.schema.DatabaseSchema;
import com.vmware.ovsdb.service.OvsdbClient;
import com.vmware.ovsdb.service.OvsdbPassiveConnectionListener;
import com.vmware.ovsdb.service.impl.OvsdbPassiveConnectionListenerImpl;
import com.vmware.ovsdb.testutils.SslUtil;
import com.vmware.ovsdb.testutils.SslUtil.SelfSignedSslContextPair;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import javax.net.ssl.SSLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// TODO: Add integration tests for active connection
// TODO: Add tests for vswitchd schema
public class OvsdbClientHardwareVtepIT {

  // This library should be completely asynchrony. It should work even with single thread
  private static final ScheduledExecutorService executorService =
      Executors.newScheduledThreadPool(1);

  private static final OvsdbPassiveConnectionListener passiveConnectionListener =
      new OvsdbPassiveConnectionListenerImpl(executorService);

  private static final int OVSDB_SERVER_COUNT = 10;

  private static final List<OvsdbClient> ovsdbClients = new ArrayList<>();

  private static final List<String> dockerIds = new ArrayList<>();

  @BeforeClass
  public static void setUpClass()
      throws DockerException, InterruptedException, CertificateException, SSLException {
    SelfSignedSslContextPair sslContextPair = SslUtil.newSelfSignedSslContextPair();
    SslContext serverSslCtx = sslContextPair.getServerSslCtx();
    SelfSignedCertificate clientCert = sslContextPair.getClientCert();
    SelfSignedCertificate serverCert = sslContextPair.getServerCert();

    String clientCertPem = getX509CertPem(clientCert.cert());
    String clientPrivateKeyPem = getPrivateKeyPem(clientCert.key());
    // Because server is self-signed. The CA cert is just the server's cert.
    String caCertPem = getX509CertPem(serverCert.cert());

    // All ovsdb servers use the same private key and certificate for testing purpose
    // DON'T DO THIS IN ANY PRODUCTION
    String entryCmd = "echo '" + clientPrivateKeyPem + "' > privkey.pem && "
        + "echo '" + clientCertPem + "' > cert.pem && "
        + "echo '" + caCertPem + "' > cacert.pem && "
        + "ovsdb-server --log-file --remote=punix:/usr/local/var/run/openvswitch/db.sock"
        + " --remote=db:hardware_vtep,Global,managers /usr/local/etc/openvswitch/vtep.db"
        + " --private-key=privkey.pem --certificate=cert.pem --ca-cert=cacert.pem";
    dockerIds.addAll(startContainers(OVSDB_SERVER_COUNT, OVS_DOCKER_IMAGE,  entryCmd));

    final String setManagerCmd = "vtep-ctl set-manager ssl:" + HOST_IP + ":" + OVSDB_PORT;
    for (String dockerId : dockerIds) {
      execCommand(dockerId, setManagerCmd);
    }

    ConnectionCallback connectionCallback = mock(ConnectionCallback.class);
    doAnswer(invocationOnMock -> {
      OvsdbClient ovsdbClient = invocationOnMock.getArgument(0);
      assertNotNull(ovsdbClient);
      ovsdbClients.add(ovsdbClient);
      return null;
    }).when(connectionCallback).connected(any());
    passiveConnectionListener
        .startListeningWithSsl(OVSDB_PORT, serverSslCtx, connectionCallback).join();

    verify(connectionCallback, timeout(VERIFY_TIMEOUT_MILLIS)
        .times(OVSDB_SERVER_COUNT)).connected(any());
    assertEquals(OVSDB_SERVER_COUNT, ovsdbClients.size());
  }

  @AfterClass
  public static void teardownClass() throws DockerException, InterruptedException {
    killContainers(dockerIds);
    passiveConnectionListener.stopListening(OVSDB_PORT).join();
  }

  @Before
  public void setUp() throws DockerException, InterruptedException {
    String purgeTablesCmd = "vtep-ctl --all destroy physical_switch;"
        + "vtep-ctl --all destroy tunnel;"
        + "vtep-ctl --all destroy physical_port;"
        + "vtep-ctl --all destroy logical_binding_stats;"
        + "vtep-ctl --all destroy logical_switch;"
        + "vtep-ctl --all destroy ucast_macs_local;"
        + "vtep-ctl --all destroy ucast_macs_remote;"
        + "vtep-ctl --all destroy mcast_macs_local;"
        + "vtep-ctl --all destroy mcast_macs_remote;"
        + "vtep-ctl --all destroy logical_router;"
        + "vtep-ctl --all destroy arp_sources_local;"
        + "vtep-ctl --all destroy arp_sources_remote;"
        + "vtep-ctl --all destroy physical_locator_set;"
        + "vtep-ctl --all destroy physical_locator;"
        + "vtep-ctl --all destroy acl_entry;"
        + "vtep-ctl --all destroy acl;";
    for (String dockerId : dockerIds) {
      execCommand(dockerId, purgeTablesCmd);
    }
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testListDatabases() throws OvsdbClientException {
    for (OvsdbClient ovsdbClient : ovsdbClients) {
      String[] expectedResult = {HARDWARE_VTEP};
      String[] actualResult = ovsdbClient.listDatabases().join();
      assertArrayEquals(expectedResult, actualResult);
    }
  }

  @Test
  public void testGetSchema() throws OvsdbClientException {
    for (OvsdbClient ovsdbClient : ovsdbClients) {
      DatabaseSchema actualResult = ovsdbClient.getSchema(HARDWARE_VTEP).join();
      assertNotNull(actualResult);
      assertEquals(HARDWARE_VTEP, actualResult.getName());
    }
  }

  @Test(timeout = TEST_TIMEOUT_MILLIS)
  public void testInsert() throws InterruptedException {
    String lsUuidName = "ls1";
    Row lsRow = new Row().stringColumn("name", "ls1")
        .stringColumn("description", "First logical switch")
        .integerColumn("tunnel_key", 5000L);

    String plUuidName = "pl1";
    Row plRow = new Row().stringColumn("encapsulation_type", "vxlan_over_ipv4")
        .stringColumn("dst_ip", "10.1.1.1");

    Row umrRow = new Row()
        .stringColumn("MAC", "00:11:22:33:44:55")
        .namedUuidColumn("logical_switch", lsUuidName)
        .namedUuidColumn("locator", plUuidName);

    List<Operation> insertOperations = ImmutableList.of(
        new Insert(LOGICAL_SWITCH, lsRow).withUuidName(lsUuidName),
        new Insert(PHYSICAL_LOCATOR, plRow, plUuidName),
        new Insert(UCAST_MACS_REMOTE, umrRow)
    );

    CountDownLatch doneSignal = new CountDownLatch(OVSDB_SERVER_COUNT);
    for (int i = 0; i < OVSDB_SERVER_COUNT; i++) {
      OvsdbClient ovsdbClient = ovsdbClients.get(i);
      new Thread(() -> {
        InsertResult[] insertResults = new InsertResult[0];
        try {
          OperationResult[] operationResults = ovsdbClient
              .transact(HARDWARE_VTEP, insertOperations).join();
          insertResults = Arrays.copyOf(
              operationResults, operationResults.length, InsertResult[].class);
        } catch (OvsdbClientException e) {
          fail(e.getMessage());
        }
        assertEquals(3, insertResults.length);

        Uuid lsUuid = insertResults[0].getUuid();
        Uuid plUuid = insertResults[1].getUuid();
        Uuid umrUuid = insertResults[2].getUuid();

        Select lsSelect = new Select(LOGICAL_SWITCH)
            .where("_uuid", Function.EQUALS, lsUuid);
        Select plSelect = new Select(PHYSICAL_LOCATOR)
            .where("_uuid", Function.EQUALS, plUuid);
        Select umrSelect = new Select(UCAST_MACS_REMOTE)
            .where("_uuid", Function.EQUALS, umrUuid);
        List<Operation> selectOperations = ImmutableList.of(
            lsSelect, plSelect, umrSelect);
        SelectResult[] selectResults = new SelectResult[0];
        try {
          OperationResult[] operationResults = ovsdbClient
              .transact(HARDWARE_VTEP, selectOperations).join();
          selectResults = Arrays.copyOf(
              operationResults, operationResults.length, SelectResult[].class);
        } catch (OvsdbClientException e) {
          fail(e.getMessage());
        }
        assertEquals(3, selectResults.length);

        for (SelectResult selectResult : selectResults) {
          assertEquals(1, selectResult.getRows().size());
        }
        Row selectedLsRow = selectResults[0].getRows().get(0);
        Row selectedPlRow = selectResults[1].getRows().get(0);
        Row selectedUmrRow = selectResults[2].getRows().get(0);
        assertRowEquals(lsRow, selectedLsRow,
            ImmutableList.of("name", "description", "tunnel_key"));
        assertRowEquals(
            plRow, selectedPlRow, ImmutableList.of("name", "encapsulation_type", "dst_ip")
        );
        assertRowEquals(umrRow, selectedUmrRow, ImmutableList.of("MAC"));
        assertEquals(lsUuid, selectedUmrRow.getUuidColumn("logical_switch"));
        assertEquals(plUuid, selectedUmrRow.getUuidColumn("locator"));

        doneSignal.countDown();
      }).start();
    }
    doneSignal.await();
  }

  // TODO: Add more tests

  private void assertRowEquals(Row expected, Row actual, List<String> columns) {
    for (String column : columns) {
      assertEquals(
          expected.getColumns().get(column), actual.getColumns().get(column)
      );
    }
  }
}
