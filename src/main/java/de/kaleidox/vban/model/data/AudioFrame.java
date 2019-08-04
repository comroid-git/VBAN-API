package de.kaleidox.vban.model.data;

import de.kaleidox.util.model.ByteArray;

/**
 * Preliminary interface for packets containing audio data.
 */
public class AudioFrame implements ByteArray {
    private final byte[] audioBytes;

    private AudioFrame(byte[] bytes) {
        audioBytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return audioBytes;
    }

    public static AudioFrame fromBytes(byte[] bytes) {
        return new AudioFrame(bytes);
    }
}
