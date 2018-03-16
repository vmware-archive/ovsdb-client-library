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
import java.io.IOException;
import org.junit.Test;

public class NamedUuidTest {

    private static final String uuidName = "uuid-name";

    private String json = "[\"named-uuid\",\"" + uuidName + "\"]";

    private NamedUuid namedUuid = new NamedUuid(uuidName);

    @Test
    public void testSerialization() throws JsonProcessingException {
        assertEquals(json, JsonUtil.serialize(namedUuid));
    }

    @Test
    public void testDeserialization() throws IOException {
        assertEquals(
            namedUuid,
            JsonUtil.deserialize(json, NamedUuid.class)
        );
    }

    @Test(expected = IOException.class)
    public void testInvalidNamedUuid1() throws IOException {
        String json = "[\"id\", \"" + uuidName + "\"]";

        JsonUtil.deserialize(json, NamedUuid.class);
    }

    @Test(expected = IOException.class)
    public void testInvalidNamedUuid2() throws IOException {
        String json = "[\"" + uuidName + "\"]";

        JsonUtil.deserialize(json, NamedUuid.class);
    }
}
