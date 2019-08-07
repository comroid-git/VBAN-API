package de.kaleidox.test.vban.packet;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.exception.InvalidPacketAttributeException;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VBANPacketHeadTest {
    private VBANPacketHead.Factory headFactory;

    @Before
    public void setUp() {
        headFactory = VBANPacketHead.Factory.builder(VBAN.Protocol.TEXT)
                .setSRValue(VBAN.BitsPerSecond.Bps150)
                .setFormatValue(VBAN.CommandFormat.UTF8)
                .setChannel((byte) 5)
                .setSamples((byte) 8)
                .setCodec(VBAN.Codec.VBCA)
                .setStreamName("Textstream")
                .build();
    }

    @Test
    public void testHeadValidity() {
        byte[] bytes = headFactory.create().getBytes();

        assertEquals(bytes[0], 'V');
        assertEquals(bytes[1], 'B');
        assertEquals(bytes[2], 'A');
        assertEquals(bytes[3], 'N');
    }

    @Test
    public void testHeaderSize() {
        assertEquals(28, headFactory.create().getBytes().length);
    }

    @Test
    public void testHeadDecomposing() throws InvalidPacketAttributeException {
        byte[] generated = headFactory.create().getBytes();

        VBANPacketHead.Decoded decoded = VBANPacketHead.decode(generated);

        assertEquals(VBAN.BitsPerSecond.Bps150, decoded.getDataRateValue());
        assertEquals(VBAN.CommandFormat.UTF8, decoded.getFormat());
        assertEquals(5, decoded.getChannel());
        assertEquals(8, decoded.getSamples());
        assertEquals(VBAN.Codec.VBCA, decoded.getCodec());
        assertEquals("Textstream", decoded.getStreamName());
    }
}
