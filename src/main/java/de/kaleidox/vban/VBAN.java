package de.kaleidox.vban;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.jetbrains.annotations.NotNull;

import static de.kaleidox.util.Util.appendByteArray;
import static de.kaleidox.util.Util.createByteArray;
import static de.kaleidox.vban.packet.VBANPacket.MAX_SIZE;

public class VBAN<D> extends OutputStream {
    public static final int DEFAULT_PORT = 6980;
    private final InetAddress address;
    private final int port;
    private VBANPacket.Factory packetFactory;
    private DatagramSocket socket;
    private byte[] buf = new byte[0];
    private boolean closed = false;

    private VBAN(VBANPacket.Factory packetFactory, InetAddress address, int port) throws SocketException {
        this.packetFactory = packetFactory;
        this.address = address;
        this.port = port;

        socket = new DatagramSocket();
    }

    public VBAN<D> sendData(D data) throws IOException {
        VBANPacket packet = packetFactory.create();
        write(packet.setData(createByteArray(data)).getBytes());
        return this;
    }

    @Override
    public void write(int b) throws IOException {
        if (buf.length + 1 > MAX_SIZE)
            throw new IOException("Byte array is too large, must be smaller than " + MAX_SIZE);
        buf = appendByteArray(buf, (byte) b);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        super.write(b);
        flush();
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        flush();
    }

    @Override
    public synchronized void flush() throws IOException {
        if (closed) throw new IOException("Stream is closed");
        if (buf.length > MAX_SIZE)
            throw new IOException("Byte array is too large, must be smaller than " + MAX_SIZE);
        socket.send(new DatagramPacket(buf, buf.length, address, port));
        buf = new byte[0];
    }

    @Override
    public void close() throws IOException {
        socket = null;
        packetFactory = null;
        super.close();

        closed = true;
    }

    public static VBAN<String> openTextStream(InetAddress address, int port) throws SocketException {
        return new VBAN<>(
                VBANPacket.Factory.builder()
                        .setHeadFactory(VBANPacketHead.defaultTextProtocolFactory())
                        .build(),
                address, port
        );
    }

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

    public final class Protocol {
        public final static int AUDIO = 0x00;
        public final static int SERIAL = 0x20;
        public final static int TEXT = 0x40;
        public final static int SERVICE = 0x60;
    }

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

    public final class Codec {
        public final static int PCM = 0x00;
        public final static int VBCA = 0x10; // VB-Audio AOIP Codec
        public final static int VBCV = 0x20; // VB-Audio VOIP Codec
        public final static int USER = 0xF0;
    }
}
