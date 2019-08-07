package de.kaleidox.vban.model.data;

import de.kaleidox.util.model.ByteArray;

/**
 * A class representing any frame of audio data received via VBAN.
 */
public class AudioFrame implements ByteArray {
    private final byte[] audioBytes;

    /**
     * Private constructor.
     * Create {@link AudioFrame}s with {@link #fromBytes(byte[])}.
     *
     * @param bytes The audio data bytes of this frame.
     */
    private AudioFrame(byte[] bytes) {
        audioBytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return audioBytes;
    }

    /**
     * Used to create a new {@link AudioFrame}.
     *
     * @param bytes The audio data bytes of this frame.
     *
     * @return A new {@link AudioFrame}.
     */
    public static AudioFrame fromBytes(byte[] bytes) {
        return new AudioFrame(bytes);
    }
}
