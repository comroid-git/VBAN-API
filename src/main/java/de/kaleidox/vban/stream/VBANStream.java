package de.kaleidox.vban.stream;

import de.kaleidox.vban.packet.VBANPacket;

public class VBANStream {
    public final static int SIZE_LIMIT = 1464;
    private final VBANPacket.Factory packetFactory;

    public VBANStream() {
        packetFactory = new VBANPacket.Factory("");
    }
}
