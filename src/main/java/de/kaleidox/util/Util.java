package de.kaleidox.util;

import java.nio.ByteBuffer;

public class Util {
    public static byte[] stringToBytesASCII(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    public static byte[] appendByteArray(byte[] target, int index, byte... append) {
        System.arraycopy(append, 0, target, index, append.length);
        return target;
    }

    public static byte[] makeByteArray(int integer) {
        return ByteBuffer.allocate(4).putInt(integer).array();
    }

    public static byte[] minSizeArray(byte[] base, int size) {
        byte[] bytes = new byte[size];
        System.arraycopy(base, 0, bytes, 0, 16);
        return bytes;
    }
}
