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
            ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool){};
            thread.setDaemon(false);
            return thread;
        }, null, false);

        socket = new DatagramSocket(socketAddress);
        dataQueue = new LinkedBlockingQueue<>();

        executor.execute(new SenderThread());
        //executor.execute(new RecieverThread());
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
}
