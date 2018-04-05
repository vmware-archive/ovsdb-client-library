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

package com.vmware.ovsdb.protocol.util;

public class OvsdbConstant {

  /* ****************** RPC Methods (Chapter 4.1) ****************** */
  public static final String LIST_DBS = "list_dbs";

  public static final String GET_SCHEMA = "get_schema";

  public static final String TRANSACT = "transact";

  public static final String CANCEL = "cancel";

  public static final String MONITOR = "monitor";

  public static final String UPDATE_NOTIFICATE = "update";

  public static final String MONITOR_CANCEL = "monitor_cancel";

  public static final String LOCK = "lock";

  public static final String STEAL = "steal";

  public static final String UNLOCK = "unlock";

  public static final String LOCKED = "locked";

  public static final String STOLEN = "stolen";

  public static final String ECHO = "echo";

  /* ****************** Notations (Chapter 3) ****************** */
  public static final String TYPE = "type";

  public static final String ENUM = "enum";

  public static final String MIN_INTEGER = "minInteger";

  public static final String MAX_INTEGER = "maxInteger";

  public static final String MIN_REAL = "minReal";

  public static final String MAX_REAL = "maxReal";

  public static final String MIN_LENGTH = "minLength";

  public static final String MAX_LENGTH = "maxLength";

  public static final String REF_TABLE = "refTable";

  public static final String REF_TYPE = "refType";

  public static final String KEY = "key";

  public static final String VALUE = "value";

  public static final String MIN = "min";

  public static final String MAX = "max";

  public static final String UNLIMITED = "unlimited";

  public static final String SET = "set";

  public static final String MAP = "map";

  public static final String UUID = "uuid";

  public static final String NAMED_UUID = "named-uuid";

  /* ****************** Operations (Chapter 5.2) ****************** */
  public static final String ABORT = "abort";

  public static final String ASSERT = "assert";

  public static final String COMMENT = "comment";

  public static final String COMMIT = "commit";

  public static final String DELETE = "delete";

  public static final String INSERT = "insert";

  public static final String MUTATE = "mutate";

  public static final String SELECT = "select";

  public static final String UPDATE = "update";

  public static final String WAIT = "wait";
}
