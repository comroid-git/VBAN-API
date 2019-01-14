package de.kaleidox.vban;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.kaleidox.util.model.Bindable;
import de.kaleidox.util.model.ByteArray;
import de.kaleidox.util.model.Factory;
import de.kaleidox.util.model.IntEnum;
import de.kaleidox.vban.model.AudioPacket;
import de.kaleidox.vban.model.FormatValue;
import de.kaleidox.vban.model.SRValue;
import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.jetbrains.annotations.NotNull;

import static de.kaleidox.vban.Util.appendByteArray;
import static de.kaleidox.vban.Util.createByteArray;
import static de.kaleidox.vban.packet.VBANPacket.Factory.builder;
import static de.kaleidox.vban.packet.VBANPacket.MAX_SIZE;

/**
 * Facade class for interacting with the API.
 *
 * @param <D> Generic-type for the
 */
public class VBAN<D> extends OutputStream {
    public static final int DEFAULT_PORT = 6980;
    private final InetAddress address;
    private final int port;
    private Factory<VBANPacket> packetFactory;
    private DatagramSocket socket;
    private byte[] buf = new byte[0];
    private boolean closed = false;

    /**
     * Private constructor. Use {@link #openByteStream(Factory, InetAddress, int)} for creating raw instances.
     *
     * @param packetFactory A factory that creates new instances of VBANPacket. See {@link VBANPacket.Factory.Builder}
     * @param address       The InetAddress to send to.
     * @param port          The port to send to.
     *
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    private VBAN(Factory<VBANPacket> packetFactory, InetAddress address, int port) throws SocketException {
        this.packetFactory = packetFactory;
        this.address = address;
        this.port = port;

        socket = new DatagramSocket();
    }

    /**
     * Tries to send the given data to the specified {@linkplain InetAddress address} on the specified {@code port}.
     *
     * @param data The data to send. Is converted to a bytearray using
     *             {@link Util#createByteArray(Object)}.
     *
     * @return The instance of the stream.
     * @throws IOException              If the stream has been {@linkplain #close() closed} before.
     * @throws IOException              See {@link DatagramSocket#send(DatagramPacket)} for details.
     * @throws IllegalArgumentException If the converted byte-array from the given data is too large.
     */
    public VBAN<D> sendData(D data) throws IOException, IllegalArgumentException {
        writeFlush(createByteArray(data));
        return this;
    }

    /**
     * Writes one byte to this stream's byte buffer, but does not send anything.
     * The byte buffer is being sent and cleared by invoking {@link #flush()}.
     * If the character-code of {@code \n} is written, the stream is flushed,
     * to ensure a 1:1 ratio of lines:writes.
     *
     * @param b The byte as an int to append.
     *
     * @throws IOException If the stream has been {@linkplain #close() closed} before.
     * @throws IOException See {@link DatagramSocket#send(DatagramPacket)} for details.
     */
    @Override
    public void write(int b) throws IOException {
        if (buf.length + 1 > MAX_SIZE)
            throw new IOException("Byte array is too large, must be smaller than " + MAX_SIZE);
        buf = appendByteArray(buf, (byte) b);
        if ((char) b == '\n') flush();
    }

    /**
     * Sends this stream's byte buffer to the specified {@linkplain InetAddress address} on the specified {@code port},
     * then clears the byte buffer.
     *
     * @throws IOException If the stream has been {@linkplain #close() closed} before.
     * @throws IOException See {@link DatagramSocket#send(DatagramPacket)} for details.
     */
    @Override
    public synchronized void flush() throws IOException {
        if (closed) throw new IOException("Stream is closed");
        if (buf.length > MAX_SIZE)
            throw new IOException("Byte array is too large, must be smaller than " + MAX_SIZE);
        byte[] bytes = packetFactory.create().setData(buf).getBytes();
        socket.send(new DatagramPacket(bytes, bytes.length, address, port));
        buf = new byte[0];
    }

    /**
     * Drops the Socket and PacketFactory object and marks this stream as {@code closed.}
     * Any attempt to send data after closing the stream will result in an {@link IOException} being thrown.
     */
    @Override
    public void close() {
        socket = null;
        packetFactory = null;

        closed = true;
    }

    /**
     * Writes an array of bytes to this stream's byte buffer, then tries to {@linkplain #flush() send} the byte buffer.
     *
     * @param b The bytes to send.
     *
     * @throws IOException If the stream has been {@linkplain #close() closed} before.
     * @throws IOException See {@link DatagramSocket#send(DatagramPacket)} for details.
     */
    public void writeFlush(@NotNull byte[] b) throws IOException {
        super.write(b);
        flush();
    }

    /**
     * Writes a subset from an array of bytes to this stream's byte buffer,
     * then tries to {@linkplain #flush() send} the byte buffer.
     *
     * @param b   The byte array to create a subset from.
     * @param off The offset for the subset in the array.
     * @param len The length of the subset to be sent in the array.
     *
     * @throws IOException If the stream has been {@linkplain #close() closed} before.
     * @throws IOException See {@link DatagramSocket#send(DatagramPacket)} for details.
     */
    public void writeFlush(@NotNull byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        flush();
    }

    /**
     * Opens a VBAN stream after the given specifications.
     *
     * @param packetFactory A factory that creates new, empty {@linkplain VBANPacket VBANPackets}.
     * @param address       The {@link InetAddress} to send this stream's data to.
     * @param port          The {@code port} to send data to.
     *
     * @return A new VBAN stream that can accept a {@link ByteArray} with {@link #sendData(Object)}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBAN<ByteArray> openByteStream(Factory<VBANPacket> packetFactory, InetAddress address, int port)
            throws SocketException {
        return new VBAN<>(packetFactory, address, port);
    }

    /**
     * Opens a VBAN Remote Text stream to the specified port on {@code localhost}.
     *
     * @param port The {@code port} to send data to.
     *
     * @return A new VBAN stream that can accept a {@link String} with {@link #sendData(Object)}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     * @throws UnknownHostException See {@link InetAddress#getLocalHost()}.
     */
    public static VBAN<String> openTextStream(int port) throws SocketException, UnknownHostException {
        return openTextStream(InetAddress.getLocalHost(), port);
    }

    /**
     * Opens a VBAN Remote Text stream to the specifications.
     *
     * @param address The {@link InetAddress} to send this stream's data to.
     * @param port    The {@code port} to send data to.
     *
     * @return A new VBAN stream that can accept a {@link String} with {@link #sendData(Object)}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBAN<String> openTextStream(InetAddress address, int port) throws SocketException {
        return new VBAN<>(builder(Protocol.TEXT).build(), address, port);
    }

    /**
     * Collection of sample rate indices, required for creating a {@link VBANPacketHead.Factory}.
     */
    public enum SampleRate implements SRValue<AudioPacket> {
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
    }

    public enum BitsPerSecond implements SRValue<String> {
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
    }

    /**
     * Collection of format values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public enum AudioFormat implements FormatValue<AudioPacket> {
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
    }

    public enum Format implements FormatValue<String> {
        BYTE8(0x00);

        private final int value;

        Format(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Collection of codec values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public final class Codec {
        public final static int PCM = 0x00;
        public final static int VBCA = 0x10; // VB-Audio AOIP Codec
        public final static int VBCV = 0x20; // VB-Audio VOIP Codec
        public final static int USER = 0xF0;
    }

    /**
     * Collection of protocol values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public static final class Protocol<T> implements Bindable<T>, IntEnum {
        public final static Protocol<AudioPacket> AUDIO = new Protocol<>(0x00);
        public final static Protocol<CharSequence> SERIAL = new Protocol<>(0x20);
        public final static Protocol<String> TEXT = new Protocol<>(0x40);
        public final static Protocol<ByteArray> SERVICE = new Protocol<>(0x60);

        private final int value;

        private Protocol(int value) {
            this.value = value;
        }

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
        public String toString() {
            return String.format("%s-Protocol(%s)", name(), Integer.toHexString(value));
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
