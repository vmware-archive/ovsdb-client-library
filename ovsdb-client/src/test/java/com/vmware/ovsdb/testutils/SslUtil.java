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

package com.vmware.ovsdb.testutils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.SSLException;
import sun.security.provider.X509Factory;

public class SslUtil {

  private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";

  private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

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

    private final SelfSignedCertificate serverCert = new SelfSignedCertificate();

    private final SelfSignedCertificate clientCert = new SelfSignedCertificate();

    private final SslContext serverSslCtx;

    private final SslContext clientSslCtx;

    SelfSignedSslContextPair() throws CertificateException, SSLException {
      serverSslCtx = SslContextBuilder.forServer(
          serverCert.certificate(), serverCert.privateKey())
          .trustManager(clientCert.certificate())
          .build();

      clientSslCtx = SslContextBuilder.forClient()
          .keyManager(clientCert.key(), clientCert.cert())
          .trustManager(serverCert.cert())
          .build();
    }

    public SelfSignedCertificate getServerCert() {
      return serverCert;
    }

    public SelfSignedCertificate getClientCert() {
      return clientCert;
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

  /**
   * Get a PEM formatted string of a X509 certificate.
   *
   * @param certificate the X509 certificate
   * @return a PEM formatted string of the certificate
   */
  public static String getX509CertPem(X509Certificate certificate)
      throws CertificateEncodingException {
    return X509Factory.BEGIN_CERT + '\n'
        + Base64.getEncoder().encodeToString(certificate.getEncoded()) + '\n'
        + X509Factory.END_CERT;
  }

  /**
   * Get a PEP formatted string of a private key.
   *
   * @param privateKey the private key
   * @return a PEM formatted string of the private key
   */
  public static String getPrivateKeyPem(PrivateKey privateKey) {
    return BEGIN_PRIVATE_KEY + '\n'
        + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + '\n'
        + END_PRIVATE_KEY;
  }
}
