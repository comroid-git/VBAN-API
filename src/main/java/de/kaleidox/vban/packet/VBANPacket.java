package de.kaleidox.vban.packet;

import de.kaleidox.util.interfaces.Factory;

public class VBANPacket {
    public final static int LIMIT = 1464;

    public static class Factory implements de.kaleidox.util.interfaces.Factory<VBANPacket> {
        private final VBANPacketHead.Factory headFactory;

        public Factory(String streamName) {
            this.headFactory = new VBANPacketHead.Factory(streamName, null);
        }

        @Override
        public VBANPacket create() {
            return null;
        }

        @Override
        public int counter() {
            return 0;
        }
    }
}
