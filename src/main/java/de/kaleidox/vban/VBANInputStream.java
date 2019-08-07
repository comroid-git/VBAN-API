package de.kaleidox.vban;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import de.kaleidox.vban.packet.VBANPacket;

public class VBANInputStream<T> extends InputStream {
    private final VBAN.Protocol<T> expectedProtocol;
    private final InetAddress address;
    private final int port;
    private DatagramSocket socket;
    private byte[] buf = new byte[0];
    private int iBuf = 0;
    private boolean closed = false;

    //todo make package-private
    public VBANInputStream(VBAN.Protocol<T> expectedProtocol, InetAddress address, int port) throws IOException {
        this.expectedProtocol = expectedProtocol;
        this.address = address;
        this.port = port;

        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        socket = new DatagramSocket(socketAddress);
        //todo socket.setSoTimeout(2000);
        //socket.send(new DatagramPacket(new byte[1], 1));
    }

    public synchronized T readData() throws IOException {
        VBANPacket.Decoded packet = readPacket();
        VBAN.Protocol<?> rcvProt;

        if (!(rcvProt = packet.getHead().getProtocol()).equals(expectedProtocol))
            throw new IllegalStateException("Expected Protocol mismatches recieved protocol " +
                    "[exp:" + expectedProtocol + ";rcv:" + rcvProt + "]");

        return expectedProtocol.createDataObject(packet.getBytes());
    }

    public synchronized VBANPacket.Decoded readPacket() throws IOException {
        long available;
        long skipped = skip(available = available());

        if (available != skipped)
            throw new AssertionError("Didn't skip as many bytes as available [ava="
                    + available + ";skp=" + skipped + "]");

        byte[] bytes = new byte[VBANPacket.MAX_SIZE];

        int nRead = read(bytes);

        /* fixme
        if (nRead != VBANPacket.MAX_SIZE)
            throw new IllegalStateException("Not a full packet was read!");
         */

        return VBANPacket.decode(bytes);
    }

    @Override
    public synchronized int read() throws IOException {
        if (closed) throw new IOException("Stream is closed");

        // if buffer not existent or end of buffer reached, recieve new bytes
        if (buf.length == 0 || iBuf >= buf.length) {
            if (socket.isClosed())
                throw new SocketException("Socket is closed");
            if (!socket.isBound())
                throw new SocketException("Socket is not bound");

            byte[] bytes = new byte[VBANPacket.MAX_SIZE];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            // blocking recieve call
            socket.receive(packet);

            buf = bytes;
            iBuf = 0;
        }

        return buf[iBuf++];
    }

    @Override
    public int available() {
        return buf.length - iBuf;
    }

    @Override
    public void close() {
        socket.close();
        socket = null;

        closed = true;
    }
}
