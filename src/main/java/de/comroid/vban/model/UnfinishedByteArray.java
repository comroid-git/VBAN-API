package de.comroid.vban.model;

import java.util.Objects;

import de.comroid.util.model.ByteArray;

import static java.lang.System.arraycopy;

/**
 * Object representing any bytearray that changes will be made to.
 */
public class UnfinishedByteArray implements ByteArray {
    private final boolean fixedSize;
    private byte[] buf;
    private int cursor;


    /**
     * Similar to {@linkplain UnfinishedByteArray new UnfinishedByteArray(int, boolean)},
     * but defaults to a resizing buffer.
     *
     * @param initSize The initial size of the internal buffer.
     */
    public UnfinishedByteArray(int initSize) {
        this(initSize, false);
    }

    /**
     * Constructor.
     *
     * @param initSize  The initial size of the internal buffer.
     * @param fixedSize Whether to allow the internal buffer to be resized.
     */
    public UnfinishedByteArray(int initSize, boolean fixedSize) {
        this.buf = new byte[initSize];
        this.fixedSize = fixedSize;
    }

    /**
     * Append more bytes to the end of this object.
     * <p>
     * If you defined the buffer to be of a fixed size in the constructor,
     * this method will throw an {@link ArrayIndexOutOfBoundsException}
     * when exceeding the current buffer size.
     *
     * @param bytes The byte/s to append.
     *
     * @throws ArrayIndexOutOfBoundsException If the buffer is of fixed size and the current buffer size is exceeded.
     */
    public void append(byte... bytes) throws ArrayIndexOutOfBoundsException {
        Objects.requireNonNull(bytes, "bytearray is null");

        int newSize = cursor + bytes.length;

        if (newSize > buf.length) {
            if (fixedSize)
                throw new ArrayIndexOutOfBoundsException("Cannot append more elements, array is at fixed size");

            // new buffer
            byte[] newBuf = new byte[buf.length * 2 + bytes.length];
            arraycopy(buf, 0, newBuf, 0, buf.length);
            buf = newBuf;
        }

        for (byte aByte : bytes)
            buf[cursor++] = aByte;
    }

    /**
     * Gets the current amount of bytes added.
     *
     * @return The current amount of bytes added.
     */
    public int length() {
        return cursor;
    }

    /**
     * Creates a final bytearray of all current bytes.
     * Does not mutate.
     *
     * @return The final bytearray.
     */
    public byte[] finish() {
        byte[] finished = new byte[length()];

        arraycopy(buf, 0, finished, 0, finished.length);

        return finished;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calling this method is equal to calling {@link #finish()}.
     */
    @Override
    public byte[] getBytes() {
        return finish();
    }

    /**
     * Gets the current buffer size.
     *
     * @return The current buffer size.
     */
    public int getBufferSize() {
        return buf.length;
    }

    /**
     * Gets the exact buffer array.
     *
     * @return The exact buffer array.
     */
    public byte[] getBufferArray() {
        return buf;
    }
}
