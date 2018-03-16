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
package com.vmware.ovsdb.jsonrpc.v1.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.net.URL;

/**
 * Utility used to serialize and deserialize JSON.
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert a {@link JsonNode} to an object of given class.
     *
     * @param jsonNode JsonNode to deserialize
     * @param klass class of the object
     * @return the deserialized object
     * @throws JsonProcessingException if fail to convert
     */
    public static <T> T treeToValue(JsonNode jsonNode, Class<T> klass)
        throws JsonProcessingException {
        return objectMapper.treeToValue(jsonNode, klass);
    }

    /**
     * Deserialize a JSON string to an object of given class.
     *
     * @param jsonString JSON string to deserialize
     * @param klass class of the object
     * @return the deserialized object
     * @throws IOException if fail to deserialize the object
     */
    public static <T> T deserialize(String jsonString, Class<T> klass) throws IOException {
        return objectMapper.readValue(jsonString, klass);
    }

    /**
     * Deserialize a JSON file to an object of given class.
     *
     * @param url URL to the JSON file to deserialize
     * @param klass class of the object
     * @return the deserialized object
     * @throws IOException if fail to deserialize the object
     */
    public static <T> T deserialize(URL url, Class<T> klass) throws IOException {
        return objectMapper.readValue(url, klass);
    }

    /**
     * Convert a {@link JsonNode} to an object of given class without throwing an exception.
     *
     * @param jsonNode JsonNode to deserialize
     * @param klass class of the object
     * @return the deserialized object or null if fail to convert
     */
    public static <T> T treeToValueNoException(JsonNode jsonNode, Class<T> klass) {
        try {
            return treeToValue(jsonNode, klass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert a {@link JsonNode} to an object of given class without throwing an exception.
     *
     * @param jsonNode JsonNode to deserialize
     * @param valueTypeRef type reference of the object
     * @return the result object or null if fail to convert
     */
    public static <T> T treeToValueNoException(JsonNode jsonNode, TypeReference<?> valueTypeRef) {
        try {
            return objectMapper.readValue(
                objectMapper.treeAsTokens(jsonNode), valueTypeRef);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deserialize a JSON string to an object of given class without throwing an exception.
     *
     * @param jsonString JSON string to deserialize
     * @param klass class of the object
     * @return the deserialized object or null if fail to deserialize
     */
    public static <T> T deserializeNoException(String jsonString, Class<T> klass) {
        try {
            return deserialize(jsonString, klass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Serialize an object to a JSON string without throwing an exception.
     *
     * @param object the object to serialize
     * @return the serialized JSON string or null if fail to serialize
     */
    public static String serializeNoException(Object object) {
        try {
            return serialize(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Serialize an object to a JSON string.
     *
     * @param object the object to serialize
     * @return the serialized JSON string or null if fail to serialize
     * @throws JsonProcessingException if fail to serialize the object
     */
    public static String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convert an object to a {@link JsonNode}.
     *
     * @param object the object to convert
     * @return the {@link JsonNode} converted from the object
     */
    public static JsonNode toJsonNode(Object object) {
        return objectMapper.valueToTree(object);
    }

    /**
     * Create an empty {@link ArrayNode}.
     *
     * @return an empty {@link ArrayNode}
     */
    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    /**
     * Read a {@link JsonNode} from a string.
     *
     * @param s the string to read from
     * @return the {@link JsonNode} object read from the string
     */
    public static JsonNode readTree(String s) throws IOException {
        return objectMapper.readTree(s);
    }
}
