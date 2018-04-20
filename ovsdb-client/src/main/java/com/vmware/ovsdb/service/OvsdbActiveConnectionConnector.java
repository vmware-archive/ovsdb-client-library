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

import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;

public interface OvsdbActiveConnectionConnector {

  /**
   * Connect to the OVSDB server on ip:port.
   *
   * @param ip the OVSDB server ip
   * @param port port to which the OVSDB is listening
   * @return a {@link CompletableFuture} that will complete with an {@link OvsdbClient}
   *         object when the connection is established
   */
  CompletableFuture<OvsdbClient> connect(String ip, int port);

  /**
   * Connect to the OVSDB server on ip:port with SSL enabled.
   *
   * @param ip the OVSDB server ip
   * @param port port to which the OVSDB is listening
   * @param sslContext the SSL context
   * @return a {@link CompletableFuture} that will complete with an {@link OvsdbClient}
   *         object when the connection is established
   */
  CompletableFuture<OvsdbClient> connectWithSsl(String ip, int port, SslContext sslContext);
}
