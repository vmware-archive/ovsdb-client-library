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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.operation.result.deserializer.OperationResultDeserializer;

/**
 * <pre>
 * {@literal
 * Regardless of whether errors occur in the database operations, the response is always a JSON-RPC
 * response with null "error" and a "result" member that is an array with the same number of
 * elements as "params".  Each element of the "result" array corresponds to the same element of the
 * "params" array.  The "result" array elements may be interpreted as follows:
 *
 * o  A JSON object that does not contain an "error" member indicates that the operation completed
 * successfully.  The specific members of the object are specified below in the descriptions of
 * individual operations.  Some operations do not produce any results, in which case the object
 * will
 * have no members.
 *
 * o  An <error> indicates that the matching operation completed with an error.
 *
 * o  A JSON null value indicates that the operation was not attempted because a prior operation
 * failed.
 * }
 * </pre>
 */
@JsonDeserialize(using = OperationResultDeserializer.class)
public abstract class OperationResult {

}
