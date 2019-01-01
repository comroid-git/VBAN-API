package de.kaleidox.test.vban;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import de.kaleidox.vban.VBAN;

public class ConnectionTest {
    public static void main(String[] args) throws IOException {
        VBAN<String> vban = VBAN.openTextStream(InetAddress.getLocalHost(), VBAN.DEFAULT_PORT);

        vban.writeFlush("bus(0).mute=1".getBytes(StandardCharsets.UTF_8));

        vban.sendData("bus(1).mute=0");
    }
}
