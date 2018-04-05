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
import com.vmware.ovsdb.protocol.operation.notation.Value;
import com.vmware.ovsdb.protocol.schema.deserializer.BaseTypeDeserializer;

/**
 * Representation of {@literal <base-type>}.
 *
 * <pre>
 * {@literal
 * <base-type>
 *   The type of a key or value in a database column.  Either an
 *   <atomic-type> or a JSON object with the following members:
 *
 *       "type": <atomic-type>            required
 *       "enum": <value>                  optional
 *       "minInteger": <integer>          optional, integers only
 *       "maxInteger": <integer>          optional, integers only
 *       "minReal": <real>                optional, reals only
 *       "maxReal": <real>                optional, reals only
 *       "minLength": <integer>           optional, strings only
 *       "maxLength": <integer>           optional, strings only
 *       "refTable": <id>                 optional, UUIDs only
 *       "refType": "strong" or "weak"    optional, only with "refTable"
 *
 *   An <atomic-type> by itself is equivalent to a JSON object with a
 *   single member "type" whose value is the <atomic-type>.
 *
 *   "enum" may be specified as a <value> whose type is a set of one or
 *   more values specified for the member "type".  If "enum" is
 *   specified, then the valid values of the <base-type> are limited to
 *   those in the <value>.
 *
 *   "enum" is mutually exclusive with the following constraints:
 *
 *       If "type" is "integer", then "minInteger" or "maxInteger" or
 *       both may also be specified, restricting the valid integer
 *       range.  If both are specified, then "maxInteger" must be
 *       greater than or equal to "minInteger".
 *
 *       If "type" is "real", then "minReal" or "maxReal" or both may
 *       also be specified, restricting the valid real range.  If both
 *       are specified, then "maxReal" must be greater than or equal to
 *       "minReal".
 *
 *       If "type" is "string", then "minLength" and "maxLength" or both
 *       may be specified, restricting the valid length of value
 *       strings.  If both are specified, then "maxLength" must be
 *       greater than or equal to "minLength".  String length is
 *       measured in characters.
 *
 *       If "type" is "uuid", then "refTable", if present, must be the
 *       name of a table within this database.  If "refTable" is
 *       specified, then "refType" may also be specified.  If "refTable"
 *       is set, the effect depends on "refType":
 *
 *       +  If "refType" is "strong" or if "refType" is omitted, the
 *          allowed UUIDs are limited to UUIDs for rows in the named
 *          table.
 *
 *       +  If "refType" is "weak", then any UUIDs are allowed, but
 *          UUIDs that do not correspond to rows in the named table will
 *          be automatically deleted.  When this situation arises in a
 *          map, both the key and the value will be deleted from the
 *          map.
 *
 *   "refTable" constraints are "deferred" constraints: they are
 *    nforced only at transaction commit time (see the "transact"
 *    equest in Section 4.1.3).  The other constraints on <base-type>
 *    re "immediate", enforced immediately by each operation.
 * }
 * </pre>
 *
 * @see IntegerBaseType
 * @see RealBaseType
 * @see BooleanBaseType
 * @see StringBaseType
 * @see UuidBaseType
 */
@JsonDeserialize(using = BaseTypeDeserializer.class)
public abstract class BaseType {

  private final AtomicType type;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Value enums;

  protected BaseType(AtomicType type) {
    this(type, null);
  }

  protected BaseType(AtomicType type, Value enums) {
    this.type = type;
    this.enums = enums;
  }

  /**
   * Create a {@link BaseType} with given {@link AtomicType} object.
   *
   * @param atomicType an {@link AtomicType} object to build the {@link BaseType}
   */
  public static BaseType atomicType(AtomicType atomicType) {
    BaseType baseType = null;
    switch (atomicType) {
      case INTEGER:
        baseType = new IntegerBaseType();
        break;
      case REAL:
        baseType = new RealBaseType();
        break;
      case BOOLEAN:
        baseType = new BooleanBaseType();
        break;
      case STRING:
        baseType = new StringBaseType();
        break;
      case UUID:
        baseType = new UuidBaseType();
        break;
      default:
        break;
    }
    return baseType;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (enums != null
        ? enums.hashCode()
        : 0);
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof BaseType)) {
      return false;
    }

    BaseType baseType = (BaseType) other;

    if (type != baseType.type) {
      return false;
    }
    return enums != null
        ? enums.equals(baseType.enums)
        : baseType.enums == null;
  }
}
