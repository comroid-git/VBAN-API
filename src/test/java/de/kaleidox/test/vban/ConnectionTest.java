package de.kaleidox.test.vban;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.kaleidox.vban.VBAN;

import static java.net.InetAddress.getLocalHost;
import static de.kaleidox.vban.VBAN.DEFAULT_PORT;

public class ConnectionTest {
    public static void main(String[] args) {
        try (VBAN<String> vban = VBAN.openTextStream(getLocalHost(), DEFAULT_PORT)) {
            int i = 0;
            while (i++ < 10) {
                vban.write("bus(0).mute=1\n".getBytes(StandardCharsets.UTF_8));
                Thread.sleep(500);
                vban.sendData("bus(0).mute=0");
                Thread.sleep(500);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
