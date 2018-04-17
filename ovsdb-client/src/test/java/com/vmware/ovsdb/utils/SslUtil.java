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

package com.vmware.ovsdb.utils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;

public class SslUtil {

  /**
   * A SSL context pair that contains one server SSL context and one client ssl context.
   * <p>
   * Both SSL contexts use a self-signed certificate.
   * The server SSL context only trusts the client certificate and vice versa.
   * Note: Because the certificates are self-signed, for each certificate,
   * the CA certificate is just itself
   * </p>
   */
  public static class SelfSignedSslContextPair {

    private SslContext serverSslCtx;

    private SslContext clientSslCtx;

    SelfSignedSslContextPair() throws CertificateException, SSLException {
      SelfSignedCertificate serverCert = new SelfSignedCertificate();
      SelfSignedCertificate clientCert = new SelfSignedCertificate();
      serverSslCtx = SslContextBuilder.forServer(
          serverCert.certificate(), serverCert.privateKey())
          .trustManager(clientCert.certificate())
          .build();

      clientSslCtx = SslContextBuilder.forClient()
          .keyManager(clientCert.key(), clientCert.cert())
          .trustManager(serverCert.cert())
          .build();
    }

    public SslContext getServerSslCtx() {
      return serverSslCtx;
    }

    public SslContext getClientSslCtx() {
      return clientSslCtx;
    }
  }

  public static SelfSignedSslContextPair newSelfSignedSslContextPair()
      throws CertificateException, SSLException {
    return new SelfSignedSslContextPair();
  }

}
