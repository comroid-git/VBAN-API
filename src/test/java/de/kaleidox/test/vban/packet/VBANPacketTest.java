package de.kaleidox.test.vban.packet;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.exception.InvalidPacketAttributeException;
import de.kaleidox.vban.packet.VBANPacket;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VBANPacketTest {
    private VBANPacket.Factory factory;

    @Before
    public void setUp() {
        VBANPacketHead.Factory headFactory = VBANPacketHead.Factory.builder(VBAN.Protocol.TEXT)
                .setSRValue(VBAN.BitsPerSecond.Bps150)
                .setFormatValue(VBAN.CommandFormat.UTF8)
                .setChannel((byte) 5)
                .setSamples((byte) 8)
                .setCodec(VBAN.Codec.VBCA)
                .setStreamName("Textstream")
                .build();

        factory = VBANPacket.Factory.builder(VBAN.Protocol.TEXT)
                .setHeadFactory(headFactory)
                .build();
    }

    @Test
    public void testHeadValidity() {
        byte[] bytes = factory.create().getBytes();

        assertEquals(bytes[0], 'V');
        assertEquals(bytes[1], 'B');
        assertEquals(bytes[2], 'A');
        assertEquals(bytes[3], 'N');
    }

    @Test
    public void testHeaderSize() {
        byte[] bytes = new byte[VBANPacket.MAX_SIZE_WITHOUT_HEAD];

        VBANPacket vbanPacket = factory.create();
        vbanPacket.setData(bytes);

        assertEquals(VBANPacket.MAX_SIZE, vbanPacket.getBytes().length);
    }

    @Test
    public void testHeadDecomposing() throws InvalidPacketAttributeException {
        byte[] bytes = new byte[VBANPacket.MAX_SIZE_WITHOUT_HEAD];

        VBANPacket generated = factory.create();
        generated.setData(bytes);

        VBANPacket.Decoded decoded = VBANPacket.decode(generated.getBytes());
        VBANPacketHead.Decoded decodedHead = decoded.getHead();

        assertEquals(VBAN.BitsPerSecond.Bps150, decodedHead.getDataRateValue());
        assertEquals(VBAN.CommandFormat.UTF8, decodedHead.getFormat());
        assertEquals(5, decodedHead.getChannel());
        assertEquals(8, decodedHead.getSamples());
        assertEquals(VBAN.Codec.VBCA, decodedHead.getCodec());
        assertEquals("Textstream", decodedHead.getStreamName());
    }
}
