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

package com.vmware.ovsdb.protocol.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.schema.deserializer.TypeDeserializer;

import java.util.Objects;

/**
 * The type of a database column.
 * <pre>
 * {@literal
 * <type>
 *   Either an <atomic-type> or a JSON object that describes the type of a database column, with the
 *   following members:
 *       "key": <base-type>                 required
 *       "value": <base-type>               optional
 *       "min": <integer>                   optional
 *       "max": <integer> or "unlimited"    optional
 *
 *   If "min" or "max" is not specified, each defaults to 1.  If "max"
 *   is specified as "unlimited", then there is no specified maximum
 *   number of elements, although the implementation will enforce some
 *   limit.  After considering defaults, "min" must be exactly 0 or
 *   exactly 1, "max" must be at least 1, and "max" must be greater
 *   than or equal to "min".
 *
 *   If "min" and "max" are both 1 and "value" is not specified, the
 *   type is the scalar type specified by "key".
 *
 *   If "min" is not 1 or "max" is not 1, or both, and "value" is not
 *   specified, the type is a set of scalar type "key".
 *
 *   If "value" is specified, the type is a map from type "key" to type
 *   "value".
 * }
 * </pre>
 */
@JsonDeserialize(using = TypeDeserializer.class)
public class Type {

  private final BaseType key;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final BaseType value;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long min;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long max;

  public Type(BaseType key) {
    this(key, null, null, null);
  }

  /**
   * Create a {@link Type} object.
   *
   * @param key value of the "key" field
   * @param value value of the "value" field
   * @param min value of the "min" field
   * @param max value of the "max" field
   */
  public Type(BaseType key, BaseType value, Long min, Long max) {
    this.key = key;
    this.value = value;
    this.min = min;
    this.max = max;
  }

  public BaseType getKey() {
    return key;
  }

  public BaseType getValue() {
    return value;
  }

  public Long getMin() {
    return min;
  }

  public Long getMax() {
    return max;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Type)) {
      return false;
    }
    Type that = (Type) other;
    return Objects.equals(key, that.getKey())
        && Objects.equals(value, that.getValue())
        && Objects.equals(min, that.getMin())
        && Objects.equals(max, that.getMax());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, min, max);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " ["
        + "key=" + key
        + ", value=" + value
        + ", min=" + min
        + ", max=" + max
        + "]";
  }
}
