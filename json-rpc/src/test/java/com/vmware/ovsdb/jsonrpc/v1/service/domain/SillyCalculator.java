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
package com.vmware.ovsdb.jsonrpc.v1.service.domain;

import com.vmware.ovsdb.jsonrpc.v1.annotation.JsonRpcServiceMethod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A silly method handler that does foolproof calculation.
 */
public class SillyCalculator {

    @JsonRpcServiceMethod
    public int add(int a, int b) {
        return a + b;
    }

    @JsonRpcServiceMethod
    public int sub(int a, int b) {
        return a - b;
    }

    @JsonRpcServiceMethod
    public int mul(int a, int b) {
        return a * b;
    }

    @JsonRpcServiceMethod(value = "list_methods")
    public List<String> listMethods() {
        return new ArrayList<>(
            Arrays.asList(
                "list_methods",
                "add",
                "sub",
                "mul",
                "echo",
                "print"
            )
        );
    }

    @JsonRpcServiceMethod
    public Object[] echo(Object... objects) {
        return objects;
    }

    @JsonRpcServiceMethod
    public void print(String msg, int num) {
        System.out.println(msg + num);
    }

    @JsonRpcServiceMethod
    public int sum(Integer... nums) {
        return Arrays.stream(nums).mapToInt(Integer::intValue).sum();
    }

    @JsonRpcServiceMethod(value = "error_method")
    public void errorMethod(String msg) {
        throw new RuntimeException(msg);
    }
}
