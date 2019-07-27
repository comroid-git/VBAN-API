package de.kaleidox.vban;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import de.kaleidox.util.model.ByteArray;

import static java.lang.System.arraycopy;

/**
 * Collection of Utility methods for the VBAN API.
 */
public class Util {
    /**
     * Creates an ASCII conform byte-array from the given String.
     *
     * @param str The string to convert.
     *
     * @return A byte-array that contains the ASCII mappings of the given String.
     */
    public static byte[] stringToBytesASCII(String str) {
        if (str == null) return new byte[0];
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    /**
     * Creates a new byte-array with the length of {@code ba1.length + ba2.length}, which will be filled with all bytes
     * of {@code ba1} and {@code ba2} in order, and then returns the new array.
     *
     * @param ba1 The first, base byte-array.
     * @param ba2 The bytes to be appended.
     *
     * @return The new, larger byte-array.
     */
    public static byte[] appendByteArray(byte[] ba1, byte... ba2) {
        byte[] bytes = new byte[ba1.length + ba2.length];

        arraycopy(ba1, 0, bytes, 0, ba1.length);
        arraycopy(ba2, 0, bytes, ba1.length, ba2.length);

        return bytes;
    }

    /**
     * Trims the base array to the given size, filling with {@code nulls} if necessary.
     *
     * @param base The base array.
     * @param size The desired size.
     *
     * @return The new, resized array.
     */
    public static byte[] trimArray(byte[] base, int size) {
        byte[] bytes = new byte[size];
        arraycopy(base, 0, bytes, 0, base.length);
        return bytes;
    }

    /**
     * Converts the given integer into a byte-array of the given size.
     *
     * @param integer The integer to convert.
     * @param size    The desired size of the array.
     *
     * @return The new byte-array.
     */
    public static byte[] intToByteArray(int integer, int size) {
        return ByteBuffer.allocate(size).putInt(integer).array();
    }

    /**
     * Converts an object into a byte-array. Used by {@link VBAN#sendData(Object)}.
     *
     * @param data The object to convert.
     *
     * @return The byte-array that has been converted out.
     */
    public static byte[] createByteArray(Object data) {
        // Must support types: String, ByteArray
        if (data instanceof String) return ((String) data).getBytes(StandardCharsets.UTF_8);
        else if (data instanceof ByteArray) return ((ByteArray) data).getBytes();
        else throw new IllegalArgumentException("Unknown Data Type! Please contact the developer.");
    }

    /**
     * Checks whether the given integer is within the given boundaries,
     * and if not, throws an {@link IllegalArgumentException}.
     *
     * @param check The int to check range of.
     * @param from  The minimum value.
     * @param to    The maximum value.
     *
     * @throws IllegalArgumentException If the integer is out of bounds.
     */
    public static void checkRange(int check, int from, int to) throws IllegalArgumentException {
        if (check < from || check > to)
            throw new IllegalArgumentException(String.format("Integer out of range. [%d;%d;%d]", from, check, to));
    }

    public static byte[] subArray(byte[] src, int iLow, int iHigh) {
        byte[] bytes = new byte[iHigh - iLow];

        System.arraycopy(src, iLow, bytes, 0, bytes.length);

        return bytes;
    }
}
