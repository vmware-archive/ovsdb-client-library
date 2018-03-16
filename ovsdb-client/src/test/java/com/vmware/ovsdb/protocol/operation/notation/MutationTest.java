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

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

public class MutationTest {

    @Test
    public void testSerialization() throws JsonProcessingException {
        String expectedString = "[\"number\",\"+=\",5]";
        Mutation mutation = new Mutation(
            "number", Mutator.SUM, Atom.integer(5));

        assertEquals(
            expectedString, JsonUtil.serialize(mutation));

        expectedString = "[\"some_set\",\"insert\",\"string\"]";
        mutation = new Mutation(
            "some_set", Mutator.INSERT, Atom.string("string"));

        assertEquals(
            expectedString, JsonUtil.serialize(mutation));
    }
}
