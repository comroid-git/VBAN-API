package de.kaleidox.vban.test;

import java.net.InetAddress;
import java.util.Arrays;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.model.DataRecievedHandler;

public class ConnectionTest {
    public static void main(String[] args) throws Exception {
        VBAN<String> vban = VBAN.openTextStream(
                new DataRecievedHandler() {
                    @Override
                    public void onDataRecieve(byte[] data) {
                        System.out.println("recieved data:");
                        System.out.println(Arrays.toString(data));
                        System.out.println(new String(data));
                    }
                }, InetAddress.getLocalHost(), 6981
        );

        vban.sendData("bus(0).mute=1");
        Thread.sleep(3000);
        vban.sendData("bus(0).mute=0");
    }
}
