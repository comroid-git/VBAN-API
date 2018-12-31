package de.kaleidox;

import java.util.Arrays;

import de.kaleidox.vban.VBAN;

import static de.kaleidox.util.Util.getInetSocketAdress;

public class ConnectionTest {
    public static void main(String[] args) throws Exception {
        VBAN<String> vban = VBAN.openTextStream(
                data -> {
                    System.out.println("recieved data:");
                    System.out.println(Arrays.toString(data));
                    System.out.println(new String(data));
                }, getInetSocketAdress(192, 168, 0, 10, 6980)
        );

        vban.sendData("bus(0).mute=1");
        Thread.sleep(3000);
        //vban.sendData("bus(0).mute=0");
    }
}
