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

package com.vmware.ovsdb.protocol.operation.result;

/**
 * This is used for operations who has an empty result. For example, "wait", "commit", "abort",
 * "comment" and "assert".
 */
public class EmptyResult extends OperationResult {

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object other) {
    return this == other || other instanceof EmptyResult;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " []";
  }
}
