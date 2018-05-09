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

package com.vmware.ovsdb.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.vmware.ovsdb.callback.LockCallback;
import com.vmware.ovsdb.callback.MonitorCallback;
import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.jsonrpc.v1.annotation.JsonRpcServiceMethod;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcException;
import com.vmware.ovsdb.jsonrpc.v1.exception.JsonRpcTransportException;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Client;
import com.vmware.ovsdb.jsonrpc.v1.service.JsonRpcV1Server;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ClientImpl;
import com.vmware.ovsdb.jsonrpc.v1.service.impl.JsonRpcV1ServerImpl;
import com.vmware.ovsdb.jsonrpc.v1.spi.JsonRpcTransporter;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.netty.JsonRpcHandler;
import com.vmware.ovsdb.protocol.methods.LockResult;
import com.vmware.ovsdb.protocol.methods.MonitorRequests;
import com.vmware.ovsdb.protocol.methods.TableUpdates;
import com.vmware.ovsdb.protocol.operation.Operation;
import com.vmware.ovsdb.protocol.operation.result.OperationResult;
import com.vmware.ovsdb.protocol.schema.DatabaseSchema;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import com.vmware.ovsdb.service.OvsdbClient;
import com.vmware.ovsdb.service.OvsdbConnectionInfo;
import com.vmware.ovsdb.util.PropertyManager;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class OvsdbClientImpl implements OvsdbClient {

  private static long RPC_TIMEOUT_SEC = PropertyManager.getLongProperty("rpc.timeout.sec", 60);

  private static final AtomicLong callId = new AtomicLong(0);

  private final OvsdbConnectionInfo connectionInfo;

  private final JsonRpcV1Client jsonRpcClient;

  private final JsonRpcV1Server jsonRpcServer;

  private final ConcurrentMap<String, MonitorCallback> monitorCallbacks = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, LockCallback> lockCallbacks = new ConcurrentHashMap<>();

  private final AtomicBoolean isActive = new AtomicBoolean(true);

  /**
   * Create an {@link OvsdbClient} instance from a Netty channel.
   *
   * @param executorService used for asynchronous operations
   * @param channel a Netty {@link Channel} object
   */
  public OvsdbClientImpl(ScheduledExecutorService executorService, Channel channel) {
    this.connectionInfo = OvsdbConnectionInfo.fromChannel(channel);

    JsonRpcTransporter transporter = new JsonRpcTransporter() {
      @Override
      public void send(JsonNode data) throws JsonRpcTransportException {
        try {
          channel.writeAndFlush(JsonUtil.serialize(data));
        } catch (Throwable ex) {
          throw new JsonRpcTransportException(ex);
        }
      }

      @Override
      public void close() {
        channel.close();
      }
    };
    jsonRpcClient = new JsonRpcV1ClientImpl(
        transporter, executorService, RPC_TIMEOUT_SEC, TimeUnit.SECONDS);
    jsonRpcServer = new JsonRpcV1ServerImpl(transporter, new OvsdbRequestHandler());

    channel.pipeline().addAfter("connectionHandler", "jsonRpcHandler",
        new JsonRpcHandler(jsonRpcClient, jsonRpcServer, executorService));
  }

  @Override
  public CompletableFuture<String[]> listDatabases() throws OvsdbClientException {
    return callMethod(OvsdbConstant.LIST_DBS, String[].class);
  }

  @Override
  public CompletableFuture<DatabaseSchema> getSchema(String dbName)
      throws OvsdbClientException {
    return callMethod(OvsdbConstant.GET_SCHEMA, DatabaseSchema.class, dbName);
  }

  @Override
  public CompletableFuture<OperationResult[]> transact(
      String dbName, List<Operation> operations
  ) throws OvsdbClientException {
    Object[] params = new Object[operations.size() + 1];
    params[0] = dbName;
    for (int i = 0; i < operations.size(); i++) {
      params[i + 1] = operations.get(i);
    }
    return callMethod(OvsdbConstant.TRANSACT, OperationResult[].class, params);
  }

  @Override
  public CompletableFuture<TableUpdates> monitor(
      String dbName, String monitorId, MonitorRequests monitorRequests,
      MonitorCallback monitorCallback
  ) throws OvsdbClientException {
    CompletableFuture<TableUpdates> completableFuture = callMethod(
        OvsdbConstant.MONITOR, TableUpdates.class, dbName, monitorId, monitorRequests
    );
    // If this monitor request succeeds, save the callback
    return completableFuture.thenApply(tableUpdates -> {
      monitorCallbacks.put(monitorId, monitorCallback);
      return tableUpdates;
    });
  }

  @Override
  public CompletableFuture<Void> cancelMonitor(String monitorId) throws OvsdbClientException {
    CompletableFuture<Void> completableFuture = callMethod(
        OvsdbConstant.MONITOR_CANCEL, Void.class, monitorId);
    return completableFuture.thenApply(result -> {
      monitorCallbacks.remove(monitorId);
      return result;
    });
  }

  @Override
  public CompletableFuture<LockResult> lock(String lockId, LockCallback lockCallback)
      throws OvsdbClientException {
    return callMethod(OvsdbConstant.LOCK, LockResult.class, lockId)
        .thenApply(lockResult -> {
          lockCallbacks.put(lockId, lockCallback);
          return lockResult;
        });
  }

  @Override
  public CompletableFuture<LockResult> steal(String lockId, LockCallback lockCallback)
      throws OvsdbClientException {
    return callMethod(OvsdbConstant.STEAL, LockResult.class, lockId)
        .thenApply(lockResult -> {
          lockCallbacks.put(lockId, lockCallback);
          return lockResult;
        });
  }

  @Override
  public CompletableFuture<Void> unlock(String lockId) throws OvsdbClientException {
    CompletableFuture<Void> completableFuture = callMethod(
        OvsdbConstant.UNLOCK, Void.class, lockId);
    return completableFuture.thenApply(result -> {
      lockCallbacks.remove(lockId);
      return result;
    });
  }

  @Override
  public OvsdbConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

  @Override
  public void shutdown() {
    if (isActive.getAndSet(false)) {
      jsonRpcClient.shutdown();
      jsonRpcServer.shutdown();

      monitorCallbacks.clear();
      lockCallbacks.clear();
    }
  }

  private String getNextId() {
    return String.valueOf(callId.getAndIncrement());
  }

  private <T> CompletableFuture<T> callMethod(
      String method, Class<T> returnType, Object... params
  ) throws OvsdbClientException {
    exceptionIfNotActive();
    try {
      return jsonRpcClient.call(getNextId(), method, returnType, params);
    } catch (JsonRpcException ex) {
      throw new OvsdbClientException(ex);
    }
  }

  private void exceptionIfNotActive() throws OvsdbClientException {
    if (!isActive.get()) {
      throw new OvsdbClientException("This OVSDB client is not active");
    }
  }

  public class OvsdbRequestHandler {

    /**
     * Handle "echo" request.
     *
     * @param params params of the echo request
     */
    @JsonRpcServiceMethod(value = OvsdbConstant.ECHO)
    public Object[] handleEcho(Object... params) {
      return params;
    }

    /**
     * Handle "update" notification.
     *
     * @param monitorId monitor id of this update
     * @param tableUpdates table updates
     */
    @JsonRpcServiceMethod(value = OvsdbConstant.UPDATE)
    public void handleUpdate(String monitorId, TableUpdates tableUpdates) {
      MonitorCallback monitorCallback = monitorCallbacks.get(monitorId);
      if (monitorCallback != null) {
        monitorCallback.update(tableUpdates);
      }
    }

    /**
     * Handle "locked" notification.
     *
     * @param lockId id of the lock that is locked
     */
    @JsonRpcServiceMethod(value = OvsdbConstant.LOCKED)
    public void handleLocked(String lockId) {
      LockCallback lockCallback = lockCallbacks.get(lockId);
      if (lockCallback != null) {
        lockCallback.locked();
      }
    }

    /**
     * Handle "stolen" notification.
     *
     * @param lockId id of the lock that is stolen
     */
    @JsonRpcServiceMethod(value = OvsdbConstant.STOLEN)
    public void handleStolen(String lockId) {
      LockCallback lockCallback = lockCallbacks.get(lockId);
      if (lockCallback != null) {
        lockCallback.stolen();
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "connectionInfo=" + connectionInfo
        + ", isActive=" + isActive
        + "]";
  }
}
