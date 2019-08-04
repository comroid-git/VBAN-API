package de.kaleidox.vban.model.data;

import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

public class MIDICommand implements CharSequence {
    private final String str;

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

    public static MIDICommand fromBytes(byte[] bytes) {
        return new MIDICommand(new String(bytes, StandardCharsets.US_ASCII));
    }
}
