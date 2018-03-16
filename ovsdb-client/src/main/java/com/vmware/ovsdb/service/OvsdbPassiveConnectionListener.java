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

public interface OvsdbPassiveConnectionListener {

    /**
     * Start listening on the specified port.
     *
     * @param port port to listen. Usually it is 6640
     * @param connectionCallback called when there is a connection from the OVSDB server
     * @return true if successfully start the listener. false otherwise.
     */
    void startListening(int port, ConnectionCallback connectionCallback);

    /**
     * Start listening on the specified port with SSL enabled.
     *
     * @param port port to listen. Usually it should  be 6640
     * @param sslContext the SSL context used for SSL connection
     * @param connectionCallback called when there is a connection from the OVSDB server
     * @return true if successfully start the listener. false otherwise.
     */
    void startListeningWithSsl(
        int port, SslContext sslContext, ConnectionCallback connectionCallback
    );

    /**
     * Stop the OVSDB manager.
     */
    void stopListening(int port);
}
