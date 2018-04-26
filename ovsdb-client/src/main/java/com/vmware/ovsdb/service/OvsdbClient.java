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

import com.vmware.ovsdb.callback.LockCallback;
import com.vmware.ovsdb.callback.MonitorCallback;
import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.protocol.methods.LockResult;
import com.vmware.ovsdb.protocol.methods.MonitorRequests;
import com.vmware.ovsdb.protocol.methods.TableUpdates;
import com.vmware.ovsdb.protocol.operation.Operation;
import com.vmware.ovsdb.protocol.operation.result.OperationResult;
import com.vmware.ovsdb.protocol.schema.DatabaseSchema;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * OVSDB client RPC methods.
 *
 * @see <a href=https://tools.ietf.org/html/rfc7047#section-4.1>RFC 7047 Section 4.1 RPC Methods</a>
 */
public interface OvsdbClient {

  /**
   * Lists an array whose elements are the names of the databases that can be accessed over this
   * management protocol connection.
   *
   * @return a {@link CompletableFuture} from which the database names array can be retrieved
   */
  CompletableFuture<String[]> listDatabases() throws OvsdbClientException;

  /**
   * Get a {@link DatabaseSchema} that describes the given database.
   *
   * @param dbName the database name
   * @return a {@link CompletableFuture} from which the schema can be got
   */
  CompletableFuture<DatabaseSchema> getSchema(String dbName) throws OvsdbClientException;

  /**
   * Run a transaction with a list of operations.
   *
   * @param dbName the database name
   * @param operations a list of operations in this transaction
   * @return a {@link CompletableFuture} from which the operation results can be retrieved
   */
  CompletableFuture<OperationResult[]> transact(String dbName, List<Operation> operations)
      throws OvsdbClientException;

  /**
   * Send a monitor request to OVSDB server.
   *
   * @param dbName the database name
   * @param monitorId a unique id that is used to match subsequent update notifications to this
   *                  request.
   * @param monitorRequests monitor requests
   * @param monitorCallback will be called when there are updates on the monitored tables
   * @return a {@link CompletableFuture} from which the initial table updates can be retrieved
   */
  CompletableFuture<TableUpdates> monitor(
      String dbName, String monitorId, MonitorRequests monitorRequests,
      MonitorCallback monitorCallback
  ) throws OvsdbClientException;

  /**
   * Send a monitor request to OVSDB server.
   *
   * @param monitorId id of the monitor to be canceled
   * @return a {@link CompletableFuture} from which the cancel result can be retrieved
   */
  CompletableFuture<Void> cancelMonitor(String monitorId) throws OvsdbClientException;

  /**
   * Send a lock request to OVSDB server.
   *
   * @param lockId id of the lock to lock
   * @param lockCallback will be called when the lock is locked or stolen
   * @return a {@link CompletableFuture} from which the lock result can be retrieved
   */
  CompletableFuture<LockResult> lock(String lockId, LockCallback lockCallback)
      throws OvsdbClientException;

  /**
   * Send a steal request to OVSDB server.
   *
   * @param lockId id of the lock to steal
   * @param lockCallback will be called when the lock is stolen
   * @return a {@link CompletableFuture} from which the steal result can be retrieved
   */
  CompletableFuture<LockResult> steal(String lockId, LockCallback lockCallback)
      throws OvsdbClientException;

  /**
   * Send a unlock request to OVSDB server.
   *
   * @param lockId id of the lock to unlock
   * @return a {@link CompletableFuture} from which the unlock result can be retrieved
   */
  CompletableFuture<Void> unlock(String lockId) throws OvsdbClientException;

  /**
   * Get the {@link OvsdbConnectionInfo} of this client.
   *
   * @return the {@link OvsdbConnectionInfo} of this client
   */
  OvsdbConnectionInfo getConnectionInfo();

  /**
   * Shut down this OVSDB client.
   */
  void shutdown();
}
