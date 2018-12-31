package de.kaleidox.vban;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;

import de.kaleidox.vban.model.DataRecievedHandler;
import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import static de.kaleidox.util.Util.createByteArray;

public class VBAN<D> {
    public static final int DEFAULT_PORT = 6980;
    private final InetAddress address;
    private final int port;
    private final DataRecievedHandler handler;
    private final VBANPacket.Factory packetFactory;
    private final DatagramSocket socket;
    private final ExecutorService executor;
    private final LinkedBlockingQueue<D> dataQueue;

    private VBAN(DataRecievedHandler handler, VBANPacket.Factory packetFactory, InetSocketAddress socketAddress)
            throws SocketException {
        this.handler = handler;
        this.packetFactory = packetFactory;
        this.address = socketAddress.getAddress();
        this.port = socketAddress.getPort();
        this.executor = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), pool -> {
            ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
            };
            thread.setDaemon(false);
            return thread;
        }, null, false);

        socket = new DatagramSocket(socketAddress);
        dataQueue = new LinkedBlockingQueue<>();

        executor.execute(new SenderThread());
        executor.execute(new RecieverThread());
    }

    public VBAN<D> sendData(D data) {
        synchronized (dataQueue) {
            dataQueue.add(data);
            dataQueue.notify();
        }
        return this;
    }

    public static VBAN<String> openTextStream(DataRecievedHandler handler, InetAddress address, int port)
            throws SocketException {
        return new VBAN<>(
                handler,
                VBANPacket.Factory.builder()
                        .setHeadFactory(VBANPacketHead.defaultTextProtocolFactory())
                        .build(),
                new InetSocketAddress(address, port)
        );
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private class SenderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (dataQueue) {
                        while (dataQueue.isEmpty()) dataQueue.wait();
                        D data = dataQueue.poll();
                        VBANPacket packet = packetFactory.create();
                        byte[] bytes = packet.setData(createByteArray(data)).getBytes();
                        System.out.printf("Sending ByteArray: [%s] %s\n", new String(bytes), Arrays.toString(bytes));
                        socket.send(new DatagramPacket(bytes, bytes.length, address, port));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private class RecieverThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    byte[] buf = new byte[VBANPacket.MAX_SIZE];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (packet.getAddress().equals(address)) handler.onDataRecieve(packet.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
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
