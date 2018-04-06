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
import io.netty.handler.ssl.SslContext;

public interface OvsdbActiveConnectionConnector {

  /**
   * Connect to the OVSDB server on ip:port.
   *
   * @param ip the OVSDB server ip
   * @param port port to which the OVSDB is listening
   * @param connectionCallback called when the connection is established
   */
  void connect(String ip, int port, ConnectionCallback connectionCallback);

  /**
   * Connect to the OVSDB server on ip:port with SSL enabled.
   *
   * @param ip the OVSDB server ip
   * @param port port to which the OVSDB is listening
   * @param sslContext the SSL context
   * @param connectionCallback called when the connection is established
   */
  void connectWithSsl(
      String ip, int port, SslContext sslContext, ConnectionCallback connectionCallback
  );
}
