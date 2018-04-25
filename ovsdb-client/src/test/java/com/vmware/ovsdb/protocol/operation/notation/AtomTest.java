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
import com.google.common.testing.EqualsTester;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class AtomTest {

  private static final String string = "IAmAString";

  private static final Long integer = 42L;

  private static final Double real = 24.518;

  private static final Boolean bool = true;

  private static final Uuid uuid = new Uuid(UUID.randomUUID());

  private static final NamedUuid namedUuid = new NamedUuid("insert_ls_1");

  private static final Atom<String> atomString = Atom.string(string);

  private static final Atom<Long> atomInteger = Atom.integer(integer);

  private static final Atom<Double> atomReal = Atom.real(real);

  private static final Atom<Boolean> atomBoolean = Atom.bool(bool);

  private static final Atom<Uuid> atomUuid = Atom.uuid(uuid);

  private static final Atom<NamedUuid> atomNamedUuid = Atom.namedUuid(
      namedUuid);

  @Test
  public void testSerialization() throws JsonProcessingException {
    // <string>
    assertEquals("\"" + string + "\"", JsonUtil.serialize(atomString));

    // <integer>
    assertEquals(integer.toString(), JsonUtil.serialize(atomInteger));

    // <real>
    assertEquals(real.toString(), JsonUtil.serialize(atomReal));

    // <boolean>
    assertEquals(bool.toString(), JsonUtil.serialize(atomBoolean));

    // <uuid>
    assertEquals("[\"uuid\",\"" + uuid.getUuid() + "\"]", JsonUtil.serialize(atomUuid));

    // <named-uuid>
    assertEquals(
        "[\"named-uuid\",\"" + namedUuid.getUuidName() + "\"]", JsonUtil.serialize(atomNamedUuid)
    );
  }

  @Test
  public void testDeserialization() throws IOException {
    // <string>
    assertEquals(atomString, JsonUtil.deserialize('\"' + string + '\"', Atom.class));

    // <integer>
    assertEquals(atomInteger, JsonUtil.deserialize(integer.toString(), Atom.class));

    // <real>
    assertEquals(atomReal, JsonUtil.deserialize(real.toString(), Atom.class));

    // <bool>
    assertEquals(atomBoolean, JsonUtil.deserialize(bool.toString(), Atom.class));

    // <uuid>
    assertEquals(
        atomUuid, JsonUtil.deserialize("[\"uuid\",\"" + uuid.getUuid() + "\"]", Atom.class)
    );

    // <named-uuid>
    assertEquals(
        atomNamedUuid,
        JsonUtil.deserialize("[\"named-uuid\",\"" + namedUuid.getUuidName() + "\"]", Atom.class)
    );
  }

  @Test(expected = IOException.class)
  public void testInvalidAtom() throws IOException {
    JsonUtil.deserialize("[\"set\",[]]", Atom.class);
  }

  @Test
  public void testEquals() {
    UUID uuid = UUID.randomUUID();
    new EqualsTester()
        .addEqualityGroup(Atom.string("string"), new Atom<>("string"))
        .addEqualityGroup(Atom.integer(42), new Atom<>(42L))
        .addEqualityGroup(Atom.real(4.2), new Atom<>(4.2))
        .addEqualityGroup(Atom.bool(true), new Atom<>(true))
        .addEqualityGroup(Atom.uuid(Uuid.of(uuid)), Atom.uuid(uuid), new Atom<>(new Uuid(uuid)))
        .addEqualityGroup(
            Atom.namedUuid("named-uuid"),
            Atom.namedUuid(new NamedUuid("named-uuid")),
            new Atom<>(new NamedUuid("named-uuid"))
        )
        .testEquals();
  }
}
