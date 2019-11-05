package de.comroid.util.model;

/**
 * Marks an object as a carrier for an array of bytes.
 * This array can be acquired by invoking {@link #getBytes()}.
 */
public interface ByteArray {
    /**
     * The byte-array, or the byte-array representation of this object.
     *
     * @return A byte array that describes this object.
     */
    byte[] getBytes();
}
