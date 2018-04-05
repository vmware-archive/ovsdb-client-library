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

package com.vmware.ovsdb.protocol.methods;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vmware.ovsdb.protocol.operation.notation.serializer.MonitorRequestsSerializer;

import java.util.Map;

/**
 * The {@literal <monitor-requests>} object maps the name of the table to be monitored to an array
 * of {@literal <monitor-request>} objects.
 */
@JsonSerialize(using = MonitorRequestsSerializer.class)
public class MonitorRequests {

  private final Map<String, MonitorRequest> monitorRequests;

  public MonitorRequests(Map<String, MonitorRequest> monitorRequests) {
    this.monitorRequests = monitorRequests;
  }

  public Map<String, MonitorRequest> getMonitorRequests() {
    return monitorRequests;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "monitorRequests=" + monitorRequests
        + "]";
  }
}
