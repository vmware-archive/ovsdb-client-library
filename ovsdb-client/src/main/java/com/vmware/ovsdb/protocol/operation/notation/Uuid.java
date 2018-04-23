package com.vmware.ovsdb.protocol.operation.notation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.ovsdb.protocol.operation.notation.deserializer.UuidDeserializer;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of {@literal <uuid>}.
 * <pre>
 * {@literal
 * <uuid>
 *   A 2-element JSON array that represents a UUID.  The first element
 *   of the array must be the string "uuid", and the second element
 *   must be a 36-character string giving the UUID in the format
 *   described by RFC 4122 [RFC4122].  For example, the following
 *   <uuid> represents the UUID 550e8400-e29b-41d4-a716-446655440000:
 *
 *   ["uuid", "550e8400-e29b-41d4-a716-446655440000"]
 * }
 * </pre>
 */
@JsonDeserialize(using = UuidDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Uuid {

  public final String uuidString = OvsdbConstant.UUID; // For serializing

  private final UUID uuid;

  public Uuid(UUID uuid) {
    this.uuid = uuid;
  }

  public static Uuid of(UUID uuid) {
    return new Uuid(uuid);
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Uuid)) {
      return false;
    }
    Uuid that = (Uuid) other;
    return Objects.equals(uuid, that.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuidString, uuid);
  }

  @Override
  public String toString() {
    return uuid.toString();
  }
}
