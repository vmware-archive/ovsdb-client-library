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

import java.net.InetAddress;
import java.security.cert.Certificate;

public class OvsdbConnectionInfo {

    private final InetAddress localAddress;

    private final int localPort;

    private final InetAddress remoteAddress;

    private final int remotePort;

    private final Certificate remoteCertificate;

    public OvsdbConnectionInfo(
        InetAddress localAddress, int localPort,
        InetAddress remoteAddress, int remotePort,
        Certificate remoteCertificate
    ) {
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.remoteCertificate = remoteCertificate;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public Certificate getRemoteCertificate() {
        return remoteCertificate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + "localAddress=" + localAddress
            + ", localPort=" + localPort
            + ", remoteAddress=" + remoteAddress
            + ", remotePort=" + remotePort
            + ", remoteCertificate=" + remoteCertificate
            + "]";
    }
}
