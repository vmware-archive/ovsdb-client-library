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

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

public class OvsdbConnectionInfo {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final InetAddress localAddress;

  private final int localPort;

  private final InetAddress remoteAddress;

  private final int remotePort;

  private final Certificate localCertificate;

  private final Certificate remoteCertificate;

  /**
   * Create a {@link OvsdbConnectionInfo} object.
   *
   * @param localAddress local address of the connection
   * @param localPort local port of the connection
   * @param remoteAddress remote address of the connection
   * @param remotePort remove port of the connection
   * @param remoteCertificate remote certificate
   */
  private OvsdbConnectionInfo(
      InetAddress localAddress, int localPort,
      InetAddress remoteAddress, int remotePort,
      Certificate localCertificate, Certificate remoteCertificate
  ) {
    this.localAddress = localAddress;
    this.localPort = localPort;
    this.remoteAddress = remoteAddress;
    this.remotePort = remotePort;
    this.localCertificate = localCertificate;
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

  public Certificate getLocalCertificate() {
    return localCertificate;
  }

  public Certificate getRemoteCertificate() {
    return remoteCertificate;
  }

  /**
   * Get the connection info from a Netty channel.
   *
   * @param channel the netty channel
   * @return an {@link OvsdbConnectionInfo} object
   */
  public static OvsdbConnectionInfo fromChannel(Channel channel) {
    InetSocketAddress remoteSocketAddress
        = (InetSocketAddress) channel.remoteAddress();
    InetAddress remoteAddress = remoteSocketAddress.getAddress();
    int remotePort = remoteSocketAddress.getPort();
    InetSocketAddress localSocketAddress
        = (InetSocketAddress) channel.localAddress();
    InetAddress localAddress = localSocketAddress.getAddress();
    int localPort = localSocketAddress.getPort();

    SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
    Certificate localCertificate = null;
    Certificate remoteCertificate = null;
    if (sslHandler != null) {
      SSLSession sslSession = sslHandler.engine().getSession();
      try {
        remoteCertificate = sslSession.getPeerCertificates()[0];
      } catch (SSLPeerUnverifiedException ex) {
        LOGGER.error("Failed to get peer certificate of channel " + channel, ex);
      }
      if (sslSession.getLocalCertificates() != null) {
        localCertificate = sslSession.getLocalCertificates()[0];
      }
    }
    return new OvsdbConnectionInfo(
        localAddress, localPort, remoteAddress, remotePort, localCertificate, remoteCertificate
    );
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
