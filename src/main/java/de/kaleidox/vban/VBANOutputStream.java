package de.kaleidox.vban;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.kaleidox.util.model.Factory;
import de.kaleidox.vban.packet.VBANPacket;

import static de.kaleidox.vban.Util.appendByteArray;
import static de.kaleidox.vban.Util.createByteArray;
import static de.kaleidox.vban.packet.VBANPacket.MAX_SIZE;

public class VBANOutputStream<T> extends OutputStream {
    private final InetAddress address;
    private final int port;
    private Factory<VBANPacket<T>> packetFactory;
    private DatagramSocket socket;
    private byte[] buf = new byte[0];
    private boolean closed = false;

    /**
     * Private constructor. Use {@link VBAN#openByteStream(Factory, InetAddress, int)} for creating raw instances.
     *
     * @param packetFactory A factory that creates new instances of VBANPacket. See {@link VBANPacket.Factory.Builder}
     * @param address       The InetAddress to send to.
     * @param port          The port to send to.
     *
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    VBANOutputStream(Factory<VBANPacket<T>> packetFactory, InetAddress address, int port)
            throws SocketException {
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
    public VBANOutputStream<T> sendData(T data) throws IOException, IllegalArgumentException {
        write(createByteArray(data));
        flush();
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
        try {
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
        packetFactory = null;

        closed = true;
    }
}
