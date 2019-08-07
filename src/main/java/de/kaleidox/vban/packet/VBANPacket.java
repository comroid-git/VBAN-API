package de.kaleidox.vban.packet;

import de.kaleidox.util.model.ByteArray;
import de.kaleidox.vban.VBAN.Protocol;
import de.kaleidox.vban.exception.InvalidPacketAttributeException;
import de.kaleidox.vban.model.UnfinishedByteArray;

import static de.kaleidox.vban.Util.subArray;

/**
 * Structural object representation of a VBAN UDP Packet.
 */
public class VBANPacket<T> implements ByteArray {
    public static final int MAX_SIZE = 1436;
    public static final int SIZE_WITHOUT_HEAD = MAX_SIZE - VBANPacketHead.SIZE;
    protected final UnfinishedByteArray unfinishedByteArray;
    protected VBANPacketHead<T> head;
    private boolean hasData = false;

    /**
     * Private constructor.
     *
     * @param head The PacketHead to attach to this packet.
     */
    private VBANPacket(VBANPacketHead<T> head) {
        this.head = head;
        this.unfinishedByteArray = new UnfinishedByteArray(MAX_SIZE, true);
    }

    public VBANPacket(VBANPacketHead<T> head, byte[] data) {
        this.head = head;
        this.unfinishedByteArray = new UnfinishedByteArray(MAX_SIZE);

        unfinishedByteArray.append(head.getBytes());
        attachData(data);
    }

    /**
     * Sets the data of this packet to the given byte-array.
     *
     * @param data The data to set.
     *
     * @return This instance.
     * @throws IllegalArgumentException If the given byte-array is too large.
     */
    public VBANPacket<T> setData(byte[] data) throws IllegalStateException, IllegalArgumentException {
        if (hasData)
            throw new IllegalStateException("Packet already has data attached");
        attachData(data);
        return this;
    }

    @Override
    public byte[] getBytes() {
        return unfinishedByteArray.getBytes();
    }

    private void attachData(byte[] data) throws IllegalArgumentException {
        if (data.length > MAX_SIZE)
            throw new IllegalArgumentException("Data is too large to be sent, must be smaller than " + MAX_SIZE);

        unfinishedByteArray.append(data);

        hasData = true;
    }

    public static VBANPacket.Decoded decode(byte[] bytes) throws InvalidPacketAttributeException {
        return new Decoded(bytes);
    }

    public static class Decoded extends VBANPacket {
        public Decoded(byte[] bytes) throws InvalidPacketAttributeException {
            //noinspection unchecked
            super(VBANPacketHead.decode(subArray(bytes, 0, VBANPacketHead.SIZE)),
                    subArray(bytes, VBANPacketHead.SIZE + 1, VBANPacket.MAX_SIZE));
        }

        public VBANPacketHead.Decoded getHead() {
            if (head instanceof VBANPacketHead.Decoded)
                return (VBANPacketHead.Decoded) head;

            throw new AssertionError("Head is not instanceof VBANPacketHead.Decoded");
        }
    }

    public static class Factory<T> implements de.kaleidox.util.model.Factory<VBANPacket<T>> {
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

        public static <T> Factory<T> protocolDefault(Protocol<T> protocol) {
            return VBANPacket.Factory.builder(protocol)
                    .setDefaultFactory()
                    .build();
        }

        public static class Builder<T> implements de.kaleidox.util.model.Builder<Factory<T>> {
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
