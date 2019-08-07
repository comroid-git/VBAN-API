package de.kaleidox.vban.model;

import java.util.Objects;

import de.kaleidox.util.model.ByteArray;

import static java.lang.System.arraycopy;

public class UnfinishedByteArray implements ByteArray {
    private byte[] buf;
    private int cursor;

    public UnfinishedByteArray(int initSize) {
        buf = new byte[initSize];
    }

    public void append(byte... bytes) {
        Objects.requireNonNull(bytes, "bytearray is null");

        int newSize = cursor + bytes.length;

        // new buffer
        if (newSize > buf.length) {
            byte[] newBuf = new byte[buf.length * 2 + bytes.length];
            arraycopy(buf, 0, newBuf, 0, buf.length);
            buf = newBuf;
        }

        for (byte aByte : bytes)
            buf[cursor++] = aByte;
    }

    public int length() {
        return cursor;
    }

    public byte[] finish() {
        byte[] finished = new byte[length()];

        arraycopy(buf, 0, finished, 0, finished.length);

        return finished;
    }

    @Override
    public byte[] getBytes() {
        return finish();
    }

    public int getBufferSize() {
        return buf.length;
    }

    public byte[] getBufferArray() {
        return buf;
    }
}
