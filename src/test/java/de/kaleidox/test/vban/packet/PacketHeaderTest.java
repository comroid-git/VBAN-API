package de.kaleidox.test.vban.packet;

import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.packet.VBANPacketHead;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PacketHeaderTest {
    private VBANPacketHead.Factory headFactory;

    @Before
    public void setUp() {
        headFactory = VBANPacketHead.defaultFactory(VBAN.Protocol.TEXT);
    }

    @Test
    public void testHeaderLayout() {
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
}
