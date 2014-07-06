package org.unidal.codegen.code;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.Random;

import org.unidal.helper.Bytes;

public class DefaultObfuscater implements Obfuscater {
   @Override
   public String encode(String src) throws Exception {
      int p = new Random().nextInt(5) + 3;

      return encode(src, p, p / 2 + 1, p * 2 + 1);
   }

   private String encode(String src, int p, int q, int k) throws Exception {
      byte[] data = padding(src);

      Bytes.forBits().swap(data, p, q);
      Bytes.forBits().mask(data, k);

      return wrapup(data, p, q, k);
   }

   private byte[] padding(String str) throws Exception {
      byte[] data = str.getBytes("utf-8");
      ByteBuffer bb = ByteBuffer.allocate(data.length + 13);

      bb.put(data);
      bb.put((byte) 0);
      bb.put(Inet4Address.getLocalHost().getAddress());
      bb.putLong(System.currentTimeMillis());

      return (byte[]) bb.flip().array();
   }

   private String wrapup(byte[] data, int p, int q, int k) {
      StringBuilder sb = new StringBuilder(data.length * 2 + 3);

      sb.append(Integer.toHexString(p | 0x08));
      sb.append(Integer.toHexString(q));
      sb.append(Integer.toHexString(k));

      for (byte d : data) {
         sb.append(Integer.toHexString(d >> 4 & 0x0F));
         sb.append(Integer.toHexString(d & 0x0F));
      }

      return sb.toString();
   }
}
