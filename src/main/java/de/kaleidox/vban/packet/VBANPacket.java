package de.kaleidox.vban.packet;

import de.kaleidox.util.model.ByteArray;
import de.kaleidox.vban.VBAN.Protocol;

import static de.kaleidox.vban.Util.appendByteArray;

/**
 * Structural object representation of a VBAN UDP Packet.
 */
public class VBANPacket<T> implements ByteArray {
    public static final int MAX_SIZE = 1436;
    private VBANPacketHead<T> head;
    private byte[] bytes;

    /**
     * Private constructor.
     *
     * @param head The PacketHead to attach to this packet.
     */
    private VBANPacket(VBANPacketHead<T> head) {
        this.head = head;
    }

    /**
     * Sets the data of this packet to the given byte-array.
     *
     * @param data The data to set.
     *
     * @return This instance.
     * @throws IllegalArgumentException If the given byte-array is too large.
     */
    public VBANPacket<T> setData(byte[] data) throws IllegalArgumentException {
        if (data.length > MAX_SIZE)
            throw new IllegalArgumentException("Data is too large to be sent! Must be smaller than " + MAX_SIZE);
        bytes = data;
        return this;
    }

    @Override
    public byte[] getBytes() {
        return appendByteArray(head.getBytes(), bytes);
    }

    public static class Factory<T> implements de.kaleidox.util.model.Factory<VBANPacket> {
        private final VBANPacketHead.Factory<T> headFactory;

        private Factory(VBANPacketHead.Factory<T> headFactory) {
            this.headFactory = headFactory;
        }

        @Override
        public VBANPacket<T> create() {
            return new VBANPacket<>(headFactory.create());
        }

        @Override
        public int counter() {
            return headFactory.counter();
        }

        public static <T> Builder<T> builder(Protocol<T> protocol) {
            return new Builder<>(protocol);
        }

        public static class Builder<T> implements de.kaleidox.util.model.Builder<Factory> {
            private final Protocol<T> protocol;
            private VBANPacketHead.Factory<T> headFactory;

            private Builder(Protocol<T> protocol) {
                this.protocol = protocol;

                setDefaultFactory();
            }

            public Builder<T> setDefaultFactory() {
                return setHeadFactory(VBANPacketHead.defaultFactory(protocol));
            }

            public VBANPacketHead.Factory getHeadFactory() {
                return headFactory;
            }

            public Builder<T> setHeadFactory(VBANPacketHead.Factory<T> headFactory) {
                this.headFactory = headFactory;
                return this;
            }

            @Override
            public Factory<T> build() {
                return new Factory<>(headFactory);
            }
        }
    }
}
