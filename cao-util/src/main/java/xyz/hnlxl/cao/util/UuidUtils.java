package xyz.hnlxl.cao.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Utilities about UUID.
 * 
 * @author hnlxl at 2021/11/15
 *
 */
public class UuidUtils {

  /**
   * Bytes to UUID
   */
  public static UUID asUuid(byte[] bytes) {
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    long firstLong = bb.getLong();
    long secondLong = bb.getLong();
    return new UUID(firstLong, secondLong);
  }

  /**
   * UUID to bytes
   */
  public static byte[] asBytes(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

}
