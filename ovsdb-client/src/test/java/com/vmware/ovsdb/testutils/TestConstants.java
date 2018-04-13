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

public class TestConstants {

  public static final String OVS_DOCKER_IMAGE = "hechaol/ovs:2.9.0";

  public static final int VERIFY_TIMEOUT_MILLIS = 5 * 1000; //  5 seconds

  public static final int TEST_TIMEOUT_MILLIS = 60 * 1000; //  1 minutes

  public static String HOST_IP = System.getProperty("host.ip");

  public static final String LOCAL_HOST = "127.0.0.1";

  public static final int OVSDB_PORT = 6640;

}
