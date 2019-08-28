package de.kaleidox.test.vban;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.VBANOutputStream;
import de.kaleidox.vban.model.data.AudioFrame;
import de.kaleidox.vban.model.data.MIDICommand;
import de.kaleidox.vban.packet.VBANPacket;

import org.junit.Test;

public class VBANOutputStreamTest {
    @Test
    public void testSendDataTypes() throws IOException {
        VBANOutputStream<AudioFrame> audioFrameVBANOutputStream = VBAN.openAudioOutputStream(1);
        audioFrameVBANOutputStream.sendData(AudioFrame.fromBytes(new byte[0]));
        audioFrameVBANOutputStream.close();

        VBANOutputStream<MIDICommand> midiCommandVBANOutputStream = VBAN.openMidiOutputStream(1);
        midiCommandVBANOutputStream.sendData(MIDICommand.fromBytes(new byte[0]));
        midiCommandVBANOutputStream.close();

        VBANOutputStream<String> stringVBANOutputStream = VBAN.openCommandOutputStream(1);
        stringVBANOutputStream.sendData("");
        stringVBANOutputStream.close();
    }

    @Test(expected = Error.class)
    public void testWriteTooLargeArray() throws IOException {
        VBANOutputStream<String> stringVBANOutputStream = VBAN.openCommandOutputStream(1);

        try {
            stringVBANOutputStream.write(new byte[VBANPacket.MAX_SIZE_WITHOUT_HEAD + 1]);
        } catch (IOException filter) {
            if (filter.getMessage().startsWith("Byte array is too large, must be smaller than "))
                throw new Error(filter);
            throw filter;
        }
    }

    @Test
    public void testStringWithinBounds() throws IOException {
        byte[] bytes = new byte[VBANPacket.MAX_SIZE_WITHOUT_HEAD];
        Arrays.fill(bytes, (byte) 'x');
        VBANOutputStream<String> vban = createTextStream();
        vban.write(bytes);
        vban.flush();
        vban.close();
    }

    @Test(expected = IOException.class)
    public void testStringTooLong() throws IOException {
        byte[] bytes = new byte[VBANPacket.MAX_SIZE_WITHOUT_HEAD + 1];
        Arrays.fill(bytes, (byte) 'x');
        VBANOutputStream<String> vban = createTextStream();
        vban.write(bytes);
        vban.flush();
        vban.close();
    }

    @Test(expected = IOException.class)
    public void testStreamClosed() throws IOException {
        VBANOutputStream<String> vban = createTextStream();
        vban.close();
        vban.sendData("x");
    }

    private static VBANOutputStream<String> createTextStream() throws UnknownHostException, SocketException {
        return VBAN.openCommandOutputStream(InetAddress.getLocalHost(), VBAN.DEFAULT_PORT);
    }
}
