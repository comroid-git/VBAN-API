package de.kaleidox.vban.test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.model.DataRecievedHandler;

import org.junit.Test;

public class PacketSizeTest {
    @Test
    public void packetSizeTest() throws UnknownHostException, SocketException {
        VBAN<String> vban = VBAN.openTextStream(
                new DataRecievedHandler() {
                    @Override
                    public void onDataRecieve(byte[] data) {
                        System.out.println(new String(data));
                    }
                }, InetAddress.getLocalHost(), VBAN.DEFAULT_PORT
        );

        vban.sendData("bus(0).mute=1");
    }
}
