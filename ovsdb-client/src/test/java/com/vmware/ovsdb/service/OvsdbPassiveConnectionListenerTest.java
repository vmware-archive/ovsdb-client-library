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

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.service.impl.OvsdbPassiveConnectionListenerImpl;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class OvsdbPassiveConnectionListenerTest {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 6641; // Use port 6641 for testing purpose

    private static final int VERIFY_TIMEOUT_MILLIS = 5000; // 5 seconds

    private static final ConnectionCallback mockConnectionCallback = mock(ConnectionCallback.class);

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private final OvsdbPassiveConnectionListener passiveConnectionListener =
        new OvsdbPassiveConnectionListenerImpl(executorService);

    private SelfSignedCertificate clientCert;

    private SelfSignedCertificate serverCert;

    @Before
    public void setUp() throws InterruptedException {
        SslContext sslContext = null;
        try {
            serverCert = new SelfSignedCertificate();
            clientCert = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(
                serverCert.certificate(), serverCert.privateKey())
                .trustManager(clientCert.certificate())
                .build();

        } catch (SSLException | CertificateException e) {
            fail();
        }
        reset(mockConnectionCallback);
        passiveConnectionListener
            .startListeningWithSsl(PORT, sslContext, mockConnectionCallback);
        TimeUnit.SECONDS.sleep(2);
    }

    @After
    public void tearDown() {
        passiveConnectionListener.stopListening(PORT);
    }

    @Test
    public void testConnectionBasic() throws Exception {
        final int expectedConnectionCnt = 10;
        List<ConnectionEmulator> connectionEmulators = new ArrayList<>();
        for (int i = 0; i < expectedConnectionCnt; i++) {
            connectionEmulators.add(newConnectionEmulator());
        }

        for (int i = 0; i < expectedConnectionCnt; i++) {
            connectionEmulators.get(i).connect(HOST, PORT);
        }

        for (int i = 0; i < expectedConnectionCnt; i++) {
            connectionEmulators.get(i).disconnect();
        }

        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).connected(ArgumentMatchers.any());
        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).disconnected(ArgumentMatchers.any());
    }

    @Test
    public void testConnectThenDisconnect() throws Exception {
        final int expectedConnectionCnt = 10;
        List<ConnectionEmulator> connectionEmulators = new ArrayList<>();
        for (int i = 0; i < expectedConnectionCnt; i++) {
            connectionEmulators.add(newConnectionEmulator());
        }

        for (int i = 0; i < expectedConnectionCnt; i++) {
            connectionEmulators.get(i).connect(HOST, PORT);
            TimeUnit.SECONDS.sleep(1);
            connectionEmulators.get(i).disconnect();
            TimeUnit.SECONDS.sleep(1);
        }

        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).connected(ArgumentMatchers.any());
        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).disconnected(ArgumentMatchers.any());

    }

    @Test
    public void testWriteInvalidJson() throws Exception {
        final int expectedConnectionCnt = 1;
        ConnectionEmulator connectionEmulator = newConnectionEmulator();
        connectionEmulator.connect(HOST, PORT);

        // Write an invalid Json to the channel. The ExceptionHandler should
        // close the channel
        connectionEmulator.write("}\"msg\":\"IAmInvalidJson\"{");

        TimeUnit.SECONDS.sleep(2);

        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).connected(ArgumentMatchers.any());
        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).disconnected(ArgumentMatchers.any());
    }

    @Test
    public void testChannelTimeout() throws Exception {
        final int expectedConnectionCnt = 1;
        ConnectionEmulator connectionEmulator = newConnectionEmulator();
        connectionEmulator.connect(HOST, PORT);

        long readIdleTimeout = PropertyManager.getLongProperty("channel.read.idle.timeout.sec", 60);
        int readIdleMax = PropertyManager.getIntProperty("channel.read.idle.max", 3);
        // Wait until the ovsdb manager closes the channel
        TimeUnit.SECONDS.sleep(readIdleTimeout * readIdleMax + 2);

        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).connected(ArgumentMatchers.any());
        verify(
            mockConnectionCallback,
            timeout(VERIFY_TIMEOUT_MILLIS).times(expectedConnectionCnt)
        ).disconnected(ArgumentMatchers.any());
    }

    private ConnectionEmulator newConnectionEmulator() throws SSLException {
        return new ConnectionEmulator(
            SslContextBuilder.forClient()
                .keyManager(clientCert.certificate(), clientCert.privateKey())
                .trustManager(serverCert.certificate()).build());
    }
}
