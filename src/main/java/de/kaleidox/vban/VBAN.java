package de.kaleidox.vban;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import static de.kaleidox.util.Util.createByteArray;

public class VBAN<D> {
    public static final int DEFAULT_PORT = 6980;
    private final InetAddress address;
    private final int port;
    private final VBANPacket.Factory packetFactory;
    private final DatagramSocket socket;
    private final LinkedBlockingQueue<D> dataQueue;

    private VBAN(VBANPacket.Factory packetFactory, InetSocketAddress socketAddress)
            throws SocketException {
        this.packetFactory = packetFactory;
        this.address = socketAddress.getAddress();
        this.port = socketAddress.getPort();

        socket = new DatagramSocket();
        dataQueue = new LinkedBlockingQueue<>();
    }

    public VBAN<D> sendData(D data) {
        try {
            VBANPacket packet = packetFactory.create();
            byte[] bytes = packet.setData(createByteArray(data)).getBytes();
            socket.send(new DatagramPacket(bytes, bytes.length, address, port));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public static VBAN<String> openTextStream(InetAddress address, int port)
            throws SocketException {
        return new VBAN<>(
                VBANPacket.Factory.builder()
                        .setHeadFactory(VBANPacketHead.defaultTextProtocolFactory())
                        .build(),
                new InetSocketAddress(address, port)
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
