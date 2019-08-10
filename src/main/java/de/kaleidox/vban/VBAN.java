package de.kaleidox.vban;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import de.kaleidox.util.model.Bindable;
import de.kaleidox.util.model.Factory;
import de.kaleidox.util.model.IntEnum;
import de.kaleidox.vban.model.DataRateValue;
import de.kaleidox.vban.model.FormatValue;
import de.kaleidox.vban.model.data.AudioFrame;
import de.kaleidox.vban.model.data.MIDICommand;
import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Facade class for interacting with the API.
 */
public final class VBAN {
    public static final int DEFAULT_PORT = 6980;

    /**
     * nope
     */
    private VBAN() {
        // nope
    }

    // output

    /**
     * Opens a new audio sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#AUDIO audio protocol} and defaults to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream audio sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<AudioFrame> openAudioOutputStream(
            int port)
            throws SocketException {
        return openAudioOutputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new audio sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#AUDIO audio protocol}.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream audio sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<AudioFrame> openAudioOutputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return openAudioOutputStream(VBANPacket.Factory.protocolDefault(Protocol.AUDIO), address, port);
    }

    /**
     * Opens a new audio sending stream.
     *
     * @param packetFactory A factory to use for creating new {@linkplain VBANPacket VBANPackets}.
     * @param address       The {@linkplain InetAddress address} to bind to.
     * @param port          The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream audio sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<AudioFrame> openAudioOutputStream(
            Factory<VBANPacket<AudioFrame>> packetFactory,
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANOutputStream<>(packetFactory, address, port);
    }

    /**
     * Opens a new midi sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#SERIAL midi protocol} and defaults to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream midi sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<MIDICommand> openMidiOutputStream(
            int port)
            throws SocketException {
        return openMidiOutputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new midi sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#SERIAL midi protocol}.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream midi sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<MIDICommand> openMidiOutputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return openMidiOutputStream(VBANPacket.Factory.protocolDefault(Protocol.SERIAL), address, port);
    }

    /**
     * Opens a new midi sending stream.
     *
     * @param packetFactory A factory to use for creating new {@linkplain VBANPacket VBANPackets}.
     * @param address       The {@linkplain InetAddress address} to bind to.
     * @param port          The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream midi sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<MIDICommand> openMidiOutputStream(
            Factory<VBANPacket<MIDICommand>> packetFactory,
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANOutputStream<>(packetFactory, address, port);
    }

    /**
     * Opens a new command sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#TEXT command protocol} and defaults to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream command sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<String> openCommandOutputStream(
            int port)
            throws SocketException {
        return openCommandOutputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new command sending stream that uses the
     * {@linkplain VBANPacket.Factory#protocolDefault(Protocol) default packet factory}
     * for the {@linkplain Protocol#TEXT command protocol}.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream command sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<String> openCommandOutputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return openCommandOutputStream(VBANPacket.Factory.protocolDefault(Protocol.TEXT), address, port);
    }

    /**
     * Opens a new command sending stream.
     *
     * @param packetFactory A factory to use for creating new {@linkplain VBANPacket VBANPackets}.
     * @param address       The {@linkplain InetAddress address} to bind to.
     * @param port          The port to bind to.
     *
     * @return A new {@linkplain VBANOutputStream command sender stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANOutputStream<String> openCommandOutputStream(
            Factory<VBANPacket<String>> packetFactory,
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANOutputStream<>(packetFactory, address, port);
    }

    // input

    /**
     * Opens a new audio receiving stream that binds to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream audio receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<AudioFrame> openAudioInputStream(
            int port)
            throws SocketException {
        return openAudioInputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new audio receiving stream.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream audio receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<AudioFrame> openAudioInputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANInputStream<>(Protocol.AUDIO, address, port);
    }

    /**
     * Opens a new midi receiving stream that binds to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream midi receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<MIDICommand> openMidiInputStream(
            int port)
            throws SocketException {
        return openMidiInputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new midi receiving stream.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream midi receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<MIDICommand> openMidiInputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANInputStream<>(Protocol.SERIAL, address, port);
    }

    /**
     * Opens a new command receiving stream that binds to the
     * {@linkplain InetAddress#getLoopbackAddress() loopback address}.
     *
     * @param port The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream command receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<String> openCommandInputStream(
            int port)
            throws SocketException {
        return openCommandInputStream(InetAddress.getLoopbackAddress(), port);
    }

    /**
     * Opens a new command receiving stream.
     *
     * @param address The {@linkplain InetAddress address} to bind to.
     * @param port    The port to bind to.
     *
     * @return A new {@linkplain VBANInputStream command receiver stream}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBANInputStream<String> openCommandInputStream(
            InetAddress address,
            int port)
            throws SocketException {
        return new VBANInputStream<>(Protocol.TEXT, address, port);
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
