package de.kaleidox.vban;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import de.kaleidox.util.model.ByteArray;
import de.kaleidox.util.model.Factory;
import de.kaleidox.vban.model.AudioRecievedHandler;
import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;
import de.kaleidox.vban.stream.VBANAudioStream;

import org.jetbrains.annotations.NotNull;
import sun.audio.AudioData;

import static de.kaleidox.vban.Util.appendByteArray;
import static de.kaleidox.vban.Util.createByteArray;
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
    private final boolean recieve;
    private Factory<VBANPacket> packetFactory;
    private DatagramSocket socket;
    private byte[] buf = new byte[0];
    private boolean closed = false;
    private ExecutorService executor;
    private VBANAudioStream audioStream;

    /**
     * Private constructor. Use {@link #openByteStream(Factory, InetAddress, int)} for creating raw instances.
     *
     * @param packetFactory A factory that creates new instances of VBANPacket. See {@link VBANPacket.Factory.Builder}
     * @param address       The InetAddress to send to.
     * @param port          The port to send to.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    private VBAN(Factory<VBANPacket> packetFactory, InetAddress address, int port) throws SocketException {
        this(packetFactory, address, port, false);
    }

    /**
     * Private constructor. Use {@link #openByteStream(Factory, InetAddress, int)} for creating raw instances.
     *
     * @param packetFactory A factory that creates new instances of VBANPacket. See {@link VBANPacket.Factory.Builder}
     * @param address       The InetAddress to send to.
     * @param port          The port to send to.
     * @param recieve       Whether the stream should be able to recieve data on the given address.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    private VBAN(Factory<VBANPacket> packetFactory, InetAddress address, int port, boolean recieve)
            throws SocketException {
        this.packetFactory = packetFactory;
        this.address = address;
        this.port = port;
        this.recieve = recieve;

        socket = recieve ? new DatagramSocket(new InetSocketAddress(address, port)) : new DatagramSocket();
        executor = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                new ForkJoinPool.ForkJoinWorkerThreadFactory() {
                    @Override
                    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                        ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
                        };
                        thread.setDaemon(false);
                        return thread;
                    }
                }, new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.printf("Uncaught exception in Thread: %s", t.getName());
                e.printStackTrace();
            }
        }, true);
        RecieverThread recieverThread = new RecieverThread();
        executor.execute(recieverThread);
        audioStream = recieverThread.stream;
    }

    public VBANAudioStream getAudioStream() {
        if (!recieve) throw new IllegalStateException("Stream is not recieving!");
        return audioStream;
    }

    public VBAN<D> addAudioRecievedHandler(AudioRecievedHandler audioRecievedHandler) {
        if (!recieve) throw new IllegalStateException("Stream is not recieving!");
        audioStream.addAudioRecievedHandler(audioRecievedHandler);
        return this;
    }

    /**
     * Tries to send the given data to the specified {@linkplain InetAddress address} on the specified {@code port}.
     *
     * @param data The data to send. Is converted to a bytearray using
     *             {@link Util#createByteArray(Object)}.
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
     *
     * @param b The byte as an int to append.
     * @throws IOException If the stream has been {@linkplain #close() closed} before.
     * @throws IOException See {@link DatagramSocket#send(DatagramPacket)} for details.
     */
    @Override
    public void write(int b) throws IOException {
        if (buf.length + 1 > MAX_SIZE)
            throw new IOException("Byte array is too large, must be smaller than " + MAX_SIZE);
        buf = appendByteArray(buf, (byte) b);
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
     * @return A new VBAN stream that can accept a {@link ByteArray} with {@link #sendData(Object)}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBAN<ByteArray> openByteStream(Factory<VBANPacket> packetFactory, InetAddress address, int port)
            throws SocketException {
        return new VBAN<>(packetFactory, address, port);
    }

    /**
     * Opens a VBAN Remote Text stream to the specifications.
     *
     * @param address The {@link InetAddress} to send this stream's data to.
     * @param port    The {@code port} to send data to.
     * @return A new VBAN stream that can accept a {@link String} with {@link #sendData(Object)}.
     * @throws SocketException See {@link DatagramSocket} constructor.
     */
    public static VBAN<String> openTextStream(InetAddress address, int port) throws SocketException {
        return new VBAN<>(
                VBANPacket.Factory.builder()
                        .setHeadFactory(VBANPacketHead.defaultTextProtocolFactory())
                        .build(),
                address, port
        );
    }

    private class RecieverThread implements Runnable {
        private final AudioStreamImpl stream;
        private boolean disabled;

        private RecieverThread() {
            stream = new AudioStreamImpl();
            disabled = false;
        }

        @Override
        public void run() {
            while (!disabled) {
                try {
                    assert socket.isBound() : disableUnboundSocket();
                    byte[] bytes = new byte[MAX_SIZE];
                    DatagramPacket packet = new DatagramPacket(bytes, MAX_SIZE);
                    socket.receive(packet);
                    stream.feed(packet.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String disableUnboundSocket() {
            disabled = true;
            return "Socket is not bound";
        }

        private class AudioStreamImpl extends VBANAudioStream {
            protected AudioStreamImpl() {
                super(executor);
            }

            @Override
            protected AudioData feed(@NotNull byte[] bytes) {
                return super.feed(bytes);
            }
        }
    }

    /**
     * Collection of sample rate indices, required for creating a {@link VBANPacketHead.Factory}.
     */
    public final class SampleRate {
        public final static int hz6000 = 0;
        public final static int hz12000 = 1;
        public final static int hz24000 = 2;
        public final static int hz48000 = 3;
        public final static int hz96000 = 4;
        public final static int hz192000 = 5;
        public final static int hz384000 = 6;

        public final static int hz8000 = 7;
        public final static int hz16000 = 8;
        public final static int hz32000 = 9;
        public final static int hz64000 = 10;
        public final static int hz128000 = 11;
        public final static int hz256000 = 12;
        public final static int hz512000 = 13;

        public final static int hz11025 = 14;
        public final static int hz22050 = 15;
        public final static int hz44100 = 16;
        public final static int hz88200 = 17;
        public final static int hz176400 = 18;
        public final static int hz352800 = 19;
        public final static int hz705600 = 20;
    }

    /**
     * Collection of protocol values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public final class Protocol {
        public final static int AUDIO = 0x00;
        public final static int SERIAL = 0x20;
        public final static int TEXT = 0x40;
        public final static int SERVICE = 0x60;
    }

    /**
     * Collection of format values, required for creating a {@link VBANPacketHead.Factory}.
     */
    public final class Format {
        public final static int BYTE8 = 0x00;
        public final static int INT16 = 0x01;
        public final static int INT24 = 0x02;
        public final static int INT32 = 0x03;
        public final static int FLOAT32 = 0x04;
        public final static int FLOAT64 = 0x05;
        public final static int BITS12 = 0x06;
        public final static int BITS10 = 0x07;
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
}
