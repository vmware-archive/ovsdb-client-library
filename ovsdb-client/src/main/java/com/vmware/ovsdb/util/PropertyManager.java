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

package com.vmware.ovsdb.util;

import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());

    private static final Properties properties = new Properties();

    private static final String PROPERTY_FILE_NAME = "ovsdb-client-library.properties";

    static {
        try {
            properties.load(
                PropertyManager.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME)
            );
        } catch (Throwable e) {
            LOGGER.error("Failed to load properties", e);
        }
    }

    private static <T> T getProperty(String propertyName, T defaultValue,
        Function<String, T> converter) {
        String value = properties.getProperty(propertyName);
        try {
            return converter.apply(value);
        } catch (Throwable e) {
            LOGGER.error("Failed to get property: " + propertyName, e);
        }
        return defaultValue;
    }

    public static int getIntProperty(String propertyName, int defaultValue) {
        return getProperty(propertyName, defaultValue, Integer::valueOf);
    }

    public static long getLongProperty(String propertyName, long defaultValue) {
        return getProperty(propertyName, defaultValue, Long::valueOf);
    }
}
