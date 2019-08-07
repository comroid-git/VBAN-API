package de.kaleidox.test.vban;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.VBANInputStream;
import de.kaleidox.vban.model.data.AudioFrame;
import de.kaleidox.vban.packet.VBANPacket;

public class ConnectionTest {
    public static void main(String[] args) throws Throwable {
        final AudioFormat af = new AudioFormat(48000, 16, 2, true, false);
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
        final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

        VBANInputStream<AudioFrame> vban = VBAN.openAudioInputStream(InetAddress.getByName("192.168.0.10"), 4194);
        line.open(af, 4096);
        line.start();
        while (true) {
            AudioFrame frame;

            try {
                frame = vban.readData();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            byte[] bytes = frame.getBytes();
            line.write(bytes, 0, bytes.length);
        }
    }

    private void m() throws Throwable {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("192.168.0.10"), 4194);
        DatagramSocket socket = new DatagramSocket(socketAddress);
        socket.connect(socketAddress);

        // later

        if (socket.isClosed())
            throw new SocketException("Socket is closed");
        if (!socket.isBound())
            throw new SocketException("Socket is not bound");
        if (!socket.isConnected())
            throw new SocketException("Socket is not connected");

        byte[] bytes = new byte[VBANPacket.MAX_SIZE];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        // blocking recieve call
        socket.receive(packet);

        // this is never reached
        byte[] buf = bytes;
        int iBuf = 0;
    }
}
