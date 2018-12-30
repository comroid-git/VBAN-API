package de.kaleidox.vban.packet;

import de.kaleidox.util.interfaces.ByteArray;

import static de.kaleidox.util.Util.appendByteArray;

public class VBANPacket implements ByteArray {
    public static final int MAX_SIZE = 1436;
    private VBANPacketHead head;
    private byte[] bytes;

    private VBANPacket(VBANPacketHead head) {
        this.head = head;
    }

    public VBANPacket setData(byte[] data) throws IllegalArgumentException {
        if (data.length > MAX_SIZE)
            throw new IllegalArgumentException("Data is too large to be sent! Must be smaller than " + MAX_SIZE);
        bytes = data;
        return this;
    }

    @Override
    public byte[] getBytes() {
        return appendByteArray(head.getBytes(), bytes);
    }

    public static class Factory implements de.kaleidox.util.interfaces.Factory<VBANPacket> {
        private final VBANPacketHead.Factory headFactory;

        private Factory(VBANPacketHead.Factory headFactory) {
            this.headFactory = headFactory;
        }

        @Override
        public VBANPacket create() {
            return new VBANPacket(headFactory.create());
        }

        @Override
        public int counter() {
            return headFactory.counter();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder implements de.kaleidox.util.interfaces.Builder<Factory> {
            private VBANPacketHead.Factory headFactory;

            private Builder() {
            }

            public VBANPacketHead.Factory getHeadFactory() {
                return headFactory;
            }

            public Builder setHeadFactory(VBANPacketHead.Factory headFactory) {
                this.headFactory = headFactory;
                return this;
            }

            @Override
            public Factory build() {
                return new Factory(headFactory);
            }
        }
    }
}
