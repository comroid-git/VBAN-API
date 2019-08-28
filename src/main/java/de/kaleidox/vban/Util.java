package de.kaleidox.vban;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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
     * Converts an object into a byte-array. Used by {@link VBANOutputStream#sendData(Object)}.
     *
     * @param data The object to convert.
     *
     * @return The byte-array that has been converted out.
     */
    public static byte[] createByteArray(Object data) {
        // Must support types: CharSequence, ByteArray
        if (data instanceof CharSequence) return ((CharSequence) data).toString().getBytes(StandardCharsets.UTF_8);
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

    /**
     * Creates a sub-array of the given array.
     *
     * @param src   The origin array.
     * @param iLow  The index at which the sub-array should start.
     * @param iHigh The index at which the sub-array should stop. Exclusive.
     *
     * @return The new sub-array.
     */
    public static byte[] subArray(byte[] src, int iLow, int iHigh) {
        byte[] bytes = new byte[iHigh - iLow];

        System.arraycopy(src, iLow, bytes, 0, bytes.length);

        return bytes;
    }

    /**
     * Creates a {@link String} from the given bytearray and the given {@link Charset}.
     * Any bytes in the bytearray that are {@code 0} are ignored.
     *
     * @param bytes   The origin bytes.
     * @param charset The charset to use for decoding the given bytearray.
     *
     * @return The decoded {@link String}.
     */
    public static String bytesToString(byte[] bytes, Charset charset) {
        StringBuilder str = new StringBuilder();

        for (byte b : bytes) {
            if (b == 0) break;

            str.append(charset.decode(ByteBuffer.wrap(new byte[]{b})));
        }

        return str.toString();
    }
}
