package de.kaleidox.vban.test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.model.DataRecievedHandler;

import org.junit.Test;

public class PacketSizeTest {
    @Test
    public void packetSizeTest() throws UnknownHostException, SocketException, InterruptedException {
        VBAN<String> vban = VBAN.openTextStream(
                new DataRecievedHandler() {
                    @Override
                    public void onDataRecieve(byte[] data) {
                        System.out.println(data.length);
                        System.out.println(Arrays.toString(data));
                        System.out.println(new String(data));
                    }
                }, InetAddress.getLocalHost(), VBAN.DEFAULT_PORT
        );

        vban.sendData("bus(0).mute=1");
        Thread.sleep(3000);
        vban.sendData("bus(0).mute=0");
    }
}
