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

import com.vmware.ovsdb.protocol.methods.TableUpdates;

/**
 * Callback for "monitor" operation.
 */
public interface MonitorCallback {

  /**
   * Called when there are updates for the corresponding monitor request.
   *
   * @param tableUpdates updates from the monitored tables
   */
  void update(TableUpdates tableUpdates);
}
