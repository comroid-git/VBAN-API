package de.kaleidox.test.vban;

import java.io.IOException;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.VBANOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static de.kaleidox.vban.VBAN.DEFAULT_PORT;

public class ConnectionTest {
    public static void main(String[] args) {
        try (VBANOutputStream<String> vban = VBAN.openTextOutputStream(DEFAULT_PORT)) {
            vban.write("bus(0).mute=1\n".getBytes(UTF_8));
            Thread.sleep(500);
            vban.sendData("bus(0).mute=0");
            Thread.sleep(500);
            vban.write("bus(0).mute=1".getBytes(UTF_8));
            vban.flush();
            Thread.sleep(500);
            vban.sendData("bus(0).mute=0");
            Thread.sleep(500);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
