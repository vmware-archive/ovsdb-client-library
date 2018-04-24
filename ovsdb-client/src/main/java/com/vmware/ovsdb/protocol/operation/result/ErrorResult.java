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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Representation of {@literal <error>}.
 *
 * <pre>
 * {@literal
 * A JSON object with the following members:
 *
 *     "error": <string>          required
 *     "details": <string>        optional
 *
 * The value of the "error" member is a short string, specified in
 * this document, that broadly indicates the class of the error.
 * Most "error" strings are specific to contexts described elsewhere
 * in this document, but the following "error" strings may appear in
 * any context where an <error> is permitted:
 *
 * "error": "resources exhausted"
 * The operation requires more resources (memory, disk, CPU, etc.)
 * than are currently available to the database server.
 *
 * "error": "I/O error"
 * Problems accessing the disk, network, or other required
 * resources prevented the operation from completing.
 *
 * Database implementations MAY use "error" strings not specified in
 * this document to indicate errors that do not fit into any of the
 * specified categories.  Optionally, an <error> MAY include a
 * "details" member, whose value is a string that describes the error
 * in more detail for the benefit of a human user or administrator.
 * This document does not specify the format or content of the
 * "details" string.  An <error> MAY also have other members that
 * describe the error in more detail.  This document does not specify
 * the names or values of these members.
 * }
 * </pre>
 *
 * @see <a href=https://tools.ietf.org/html/rfc7047#section-3.1>OVSDB Management Protocol 4.1</a>
 */
public class ErrorResult extends OperationResult {

  private String error;

  private String details;

  @JsonCreator
  public ErrorResult(
      @JsonProperty(value = "error", required = true) String error,
      @JsonProperty(value = "details", required = true) String details
  ) {
    this.error = error;
    this.details = details;
  }

  public String getError() {
    return error;
  }

  public String getDetails() {
    return details;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ErrorResult)) {
      return false;
    }
    ErrorResult that = (ErrorResult) other;
    return Objects.equals(error, that.getError())
        && Objects.equals(details, that.getDetails());
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, details);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "error=" + error
        + ", details=" + details
        + "]";
  }
}
