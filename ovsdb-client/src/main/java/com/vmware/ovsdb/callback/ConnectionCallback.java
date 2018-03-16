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

package com.vmware.ovsdb.callback;

import com.vmware.ovsdb.service.OvsdbClient;

/**
 * Callback that is called when an OVSDB server is connected / disconnected to the manager.
 */
public interface ConnectionCallback {

    /**
     * Called when an OVSDB server is connected to the manager.
     *
     * @param ovsdbClient the {@link OvsdbClient} that can be used to communicate with the server
     */
    void connected(OvsdbClient ovsdbClient);

    /**
     * Called when an OVSDB server is disconnected from the manager.
     *
     * @param ovsdbClient the {@link OvsdbClient} that is used to communicate with the server
     */
    void disconnected(OvsdbClient ovsdbClient);
}
