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

package com.vmware.ovsdb.protocol.operation.notation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.operation.notation.deserializer.ValueDeserializer;

/**
 * <pre>
 * {@literal
 * <value>
 *   A JSON value that represents the value of a column in a table row,
 *   one of <atom>, <set>, or <map>.
 * }
 * </pre>
 *
 * @see Atom
 * @see Set
 * @see Map
 */
@JsonDeserialize(using = ValueDeserializer.class)
public abstract class Value {

    protected Value() {
    }
}
