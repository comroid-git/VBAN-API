package de.comroid.vban;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import de.comroid.util.model.Bindable;
import de.comroid.util.model.IntEnum;
import de.comroid.vban.model.DataRateValue;
import de.comroid.vban.model.FormatValue;
import de.comroid.vban.model.data.AudioFrame;
import de.comroid.vban.model.data.MIDICommand;
import de.comroid.vban.packet.VBANPacketHead;

import lombok.SneakyThrows;
import org.comroid.api.DelegateStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Facade class for interacting with the API.
 */
public final class VBAN<T> {
    public static final int DEFAULT_PORT = 6980;
    private final VBAN.Protocol<T> protocol;
    private final InetAddress address;
    private final int port;
    private DatagramSocket socket;

    @SneakyThrows
    public VBAN(Protocol<T> protocol, InetAddress address, int port, DelegateStream.Capability type) {
        this.protocol = protocol;
        this.address = address;
        this.port = port;

        var socketAddress = new InetSocketAddress(address, port);
        socket = new DatagramSocket(socketAddress);

        switch (type) {
            case Input:
                socket.bind(socketAddress);
                new DelegateStream.Input(socket);
                break;
            case Output:
                socket.connect(socketAddress);
                break;
            default: throw new IllegalArgumentException("Unsupported capability type: " + type);
        }
    }

    /**
     * Collection of protocol values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public static abstract class Protocol<T> implements Bindable<T>, IntEnum {
        public final static Protocol<AudioFrame> AUDIO = new Protocol<AudioFrame>(0x00) {
            @Override
            public AudioFrame createDataObject(byte[] bytes) {
                return AudioFrame.fromBytes(bytes);
            }
        };
        public final static Protocol<MIDICommand> SERIAL = new Protocol<MIDICommand>(0x20) {
            @Override
            public MIDICommand createDataObject(byte[] bytes) {
                return MIDICommand.fromBytes(bytes);
            }
        };
        public final static Protocol<String> TEXT = new Protocol<String>(0x40) {
            @Override
            public String createDataObject(byte[] bytes) {
                return new String(bytes, StandardCharsets.US_ASCII);
            }
        };
        public final static Protocol<byte[]> SERVICE = new Protocol<byte[]>(0x60) {
            @Override
            public byte[] createDataObject(final byte[] bytes) {
                return bytes;
            }
        };

        private final int value;

        private Protocol(int value) {
            this.value = value;
        }

        public abstract T createDataObject(byte[] bytes);

        public String name() {
            switch (value) {
                case 0x00:
                    return "AUDIO";
                case 0x20:
                    return "SERIAL";
                case 0x40:
                    return "TEXT";
                case 0x60:
                    return "SERVICE";
            }

            throw new AssertionError("Unknown protocol: " + this.toString());
        }

        @Override
        public int getValue() {
            return value;
        }

        public boolean isAudio() {
            return value == 0x00;
        }

        public boolean isSerial() {
            return value == 0x20;
        }

        public boolean isText() {
            return value == 0x40;
        }

        public boolean isService() {
            return value == 0x60;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Protocol)
                return ((Protocol) obj).value == value;

            return false;
        }

        @Override
        public String toString() {
            return String.format("%s-Protocol(%s)", name(), Integer.toHexString(value));
        }

        public static Protocol<?> byValue(int value) {
            switch (value) {
                case 0x00:
                    return AUDIO;
                case 0x20:
                    return SERIAL;
                case 0x40:
                    return TEXT;
                case 0x60:
                    return SERVICE;
            }

            throw new AssertionError("Unknown protocol value: " + Integer.toHexString(value));
        }
    }

    /**
     * Collection of codec values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public static final class Codec {
        public final static int PCM = 0x00;
        public final static int VBCA = 0x10; // VB-Audio AOIP Codec
        public final static int VBCV = 0x20; // VB-Audio VOIP Codec
        public final static int USER = 0xF0;
    }

    /**
     * Collection of sample rate indices, required for creating a {@link VBANPacketHead.Factory}.
     */
    public enum SampleRate implements DataRateValue<AudioFrame> {
        Hz6000,
        Hz12000,
        Hz24000,
        Hz48000,
        Hz96000,
        Hz192000,
        Hz384000,

        Hz8000,
        Hz16000,
        Hz32000,
        Hz64000,
        Hz128000,
        Hz256000,
        Hz512000,

        Hz11025,
        Hz22050,
        Hz44100,
        Hz88200,
        Hz176400,
        Hz352800,
        Hz705600;

        @Override
        public int getValue() {
            return ordinal();
        }

        @Override
        public <R> boolean isType(Class<R> type) {
            return SampleRate.class.isAssignableFrom(type);
        }

        @Override
        public @NotNull SampleRate asSampleRate() {
            return this;
        }

        @Override
        public @Nullable BitsPerSecond asBitsPerSecond() {
            return null;
        }

        public static SampleRate byValue(int value) {
            return values()[value];
        }
    }

    public enum BitsPerSecond implements DataRateValue<CharSequence> {
        Bps0,
        Bps110,
        Bps150,
        Bps300,
        Bps600,
        Bps1200,
        Bps2400,

        Bps4800,
        Bps9600,
        Bps14400,
        Bps19200,
        Bps31250,
        Bps38400,
        Bps57600,

        Bps115200,
        Bps128000,
        Bps230400,
        Bps250000,
        Bps256000,
        Bps460800,
        Bps921600,

        Bps1000000,
        Bps1500000,
        Bps2000000,
        Bps3000000;

        @Override
        public int getValue() {
            return ordinal();
        }

        @Override
        public <R> boolean isType(Class<R> type) {
            return BitsPerSecond.class.isAssignableFrom(type);
        }

        @Override
        public @Nullable SampleRate asSampleRate() {
            return null;
        }

        @Override
        public @NotNull BitsPerSecond asBitsPerSecond() {
            return this;
        }

        public static BitsPerSecond byValue(int value) {
            return values()[value];
        }
    }

    /**
     * Collection of format values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public enum AudioFormat implements FormatValue<AudioFrame> {
        BYTE8(0x00),
        INT16(0x01),
        INT24(0x02),
        INT32(0x03),
        FLOAT32(0x04),
        FLOAT64(0x05),
        BITS12(0x06),
        BITS10(0x07);

        private final int value;

        AudioFormat(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public <R> boolean isType(Class<R> type) {
            return AudioFormat.class.isAssignableFrom(type);
        }

        @Override
        public @NotNull AudioFormat asAudioFormat() {
            return this;
        }

        @Override
        public @Nullable CommandFormat asCommandFormat() {
            return null;
        }

        @Override
        public @Nullable Format asFormat() {
            return null;
        }

        public static AudioFormat byValue(int value) {
            for (AudioFormat x : values())
                if (x.value == value)
                    return x;

            throw new AssertionError("Unknown AudioFormat value: " + Integer.toHexString(value));
        }
    }

    public enum CommandFormat implements FormatValue<String> {
        ASCII(0x00),
        UTF8(0x10),
        WCHAR(0x20);

        private final int value;

        CommandFormat(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public <R> boolean isType(Class<R> type) {
            return CommandFormat.class.isAssignableFrom(type);
        }

        @Override
        public @Nullable AudioFormat asAudioFormat() {
            return null;
        }

        @Override
        public @NotNull CommandFormat asCommandFormat() {
            return this;
        }

        @Override
        public @Nullable Format asFormat() {
            return null;
        }

        public static <T> CommandFormat byValue(int value) {
            for (CommandFormat x : values())
                if (x.value == value)
                    return x;

            throw new AssertionError("Unknown Format value: " + Integer.toHexString(value));
        }
    }

    public enum Format implements FormatValue<CharSequence> {
        BYTE8(0x00);

        private final int value;

        Format(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public <R> boolean isType(Class<R> type) {
            return Format.class.isAssignableFrom(type);
        }

        @Override
        public @Nullable AudioFormat asAudioFormat() {
            return null;
        }

        @Override
        public @Nullable CommandFormat asCommandFormat() {
            return null;
        }

        @Override
        public @NotNull Format asFormat() {
            return this;
        }

        public static <T> Format byValue(int value) {
            for (Format x : values())
                if (x.value == value)
                    return x;

            throw new AssertionError("Unknown Format value: " + Integer.toHexString(value));
        }
    }
}
