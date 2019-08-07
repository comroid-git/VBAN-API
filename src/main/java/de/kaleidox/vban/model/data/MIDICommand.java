package de.kaleidox.vban.model.data;

import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

/**
 * A class representing any frame of MIDI Commands received via VBAN.
 */
public class MIDICommand implements CharSequence {
    private final String str;

    /**
     * Private constructor.
     * Create {@link MIDICommand}s with {@link #fromBytes(byte[])}.
     *
     * @param str The string that forms the MIDI Commands.
     */
    private MIDICommand(String str) {
        this.str = str;

        // todo: Implement Midi Building methods?
    }

    @Override
    public int length() {
        return str.length();
    }

    @Override
    public char charAt(int index) {
        return str.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return str.substring(start, end);
    }

    @NotNull
    @Override
    public String toString() {
        return str;
    }

    /**
     * Used to create a new {@link MIDICommand}.
     *
     * @param bytes The midi data bytes for this object.
     *
     * @return A new {@link MIDICommand}.
     */
    public static MIDICommand fromBytes(byte[] bytes) {
        return new MIDICommand(new String(bytes, StandardCharsets.US_ASCII));
    }
}
