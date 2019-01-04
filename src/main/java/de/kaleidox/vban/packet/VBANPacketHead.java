package de.kaleidox.vban.packet;

import de.kaleidox.util.model.ByteArray;
import de.kaleidox.vban.VBAN;
import de.kaleidox.vban.VBAN.Codec;
import de.kaleidox.vban.VBAN.Format;
import de.kaleidox.vban.VBAN.Protocol;
import de.kaleidox.vban.VBAN.SampleRate;

import org.intellij.lang.annotations.MagicConstant;

import static de.kaleidox.vban.Util.appendByteArray;
import static de.kaleidox.vban.Util.checkRange;
import static de.kaleidox.vban.Util.intToByteArray;
import static de.kaleidox.vban.Util.stringToBytesASCII;
import static de.kaleidox.vban.Util.trimArray;

public class VBANPacketHead implements ByteArray {
    public final static int SIZE = 28;

    private final byte[] bytes;

    private VBANPacketHead(@MagicConstant(flagsFromClass = Protocol.class) int protocol,
                           @MagicConstant(flagsFromClass = SampleRate.class) int sampleRateIndex,
                           int samples,
                           int channel,
                           @MagicConstant(flagsFromClass = Format.class) int format,
                           @MagicConstant(flagsFromClass = Codec.class) int codec,
                           String streamName,
                           int frameCounter) {
        byte[] bytes = new byte[0];
        checkRange(samples, 0, 255);
        checkRange(channel, 0, 255);

        bytes = appendByteArray(bytes, "VBAN".getBytes());
        bytes = appendByteArray(bytes, (byte) (protocol | sampleRateIndex));
        bytes = appendByteArray(bytes, (byte) samples, (byte) channel);
        bytes = appendByteArray(bytes, (byte) (format | codec));
        bytes = appendByteArray(bytes, trimArray(stringToBytesASCII(streamName), 16));
        bytes = appendByteArray(bytes, intToByteArray(frameCounter, 4));

        this.bytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Creates a Factory with the default settings for audio streams.
     *
     * @param channel The number of channels to be supported by the stream.
     * @return A new Factory instance.
     * @throws UnsupportedOperationException Always, because Audio communication is not implemented yet.
     */
    public static Factory defaultAudioProtocolFactory(int channel) throws UnsupportedOperationException {
        //noinspection ConstantConditions TODO Implement Serial Communication
        if (true) throw new UnsupportedOperationException();
        return VBANPacketHead.Factory.builder()
                .setProtocol(Protocol.AUDIO)
                .setChannel((byte) channel)
                .setSamples((byte) 255)
                .build();
    }

    /**
     * Creates a Factory with the default settings for text streams.
     *
     * @return A new Factory instance.
     */
    public static Factory defaultTextProtocolFactory() {
        return VBANPacketHead.Factory.builder()
                .setProtocol(Protocol.TEXT)
                .setChannel((byte) 0)
                .setSamples((byte) 0)
                .setStreamName("Command1")
                .setSampleRate(18)
                .build();
    }

    /**
     * Creates a Factory with the default settings for serial streams.
     *
     * @return A new Factory instance.
     * @throws UnsupportedOperationException Always, because Serial communication is not implemented yet.
     */
    public static Factory defaultSerialProtocolFactory() throws UnsupportedOperationException {
        //noinspection ConstantConditions TODO Implement Serial Communication
        if (true) throw new UnsupportedOperationException();
        return Factory.builder()
                .setProtocol(Protocol.SERIAL)
                .setChannel((byte) 0)
                .setSamples((byte) 0)
                .setFormat(VBAN.Format.BYTE8)
                .build();
    }

    /**
     * Creates a Factory with the default settings for service streams.
     *
     * @return A new Factory instance.
     * @throws UnsupportedOperationException Always, because Service communication is not implemented yet.
     */
    public static Factory defaultServiceProtocolFactory() throws UnsupportedOperationException {
        //noinspection ConstantConditions TODO Implement Service Communication
        if (true) throw new UnsupportedOperationException();
        return VBANPacketHead.Factory.builder()
                .setProtocol(Protocol.SERVICE)
                .setChannel((byte) 0)
                .setSamples((byte) 0)
                .build();
    }

    public static class Factory implements de.kaleidox.util.model.Factory<VBANPacketHead> {
        @MagicConstant(valuesFromClass = Protocol.class)
        private final int protocol;
        @MagicConstant(valuesFromClass = SampleRate.class)
        private final int sampleRate;
        private final int samples;
        private final int channel;
        @MagicConstant(valuesFromClass = Format.class)
        private final int format;
        @MagicConstant(valuesFromClass = Codec.class)
        private final int codec;
        private final String streamName;
        private int counter = 1;

        private Factory(int protocol, int sampleRate, int samples, int channel, int format, int codec, String streamName) {
            this.protocol = protocol;
            this.sampleRate = sampleRate;
            this.samples = samples;
            this.channel = channel;
            this.format = format;
            this.codec = codec;
            this.streamName = streamName;
        }

        @Override
        public synchronized VBANPacketHead create() {
            return new VBANPacketHead(protocol, sampleRate, samples, channel, format, codec, streamName, counter++);
        }

        @Override
        public synchronized int counter() {
            return counter;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder implements de.kaleidox.util.model.Builder<Factory> {
            @MagicConstant(valuesFromClass = Protocol.class)
            private int protocol = -1;
            @MagicConstant(valuesFromClass = SampleRate.class)
            private int sampleRate = SampleRate.hz48000;
            private int samples = 255;
            private int channel = 2;
            @MagicConstant(valuesFromClass = Format.class)
            private int format = Format.INT16;
            @MagicConstant(valuesFromClass = Codec.class)
            private int codec = Codec.PCM;
            private String streamName = null;

            private Builder() {
            }

            public int getProtocol() {
                return protocol;
            }

            public Builder setProtocol(int protocol) {
                this.protocol = protocol;
                return this;
            }

            public int getSampleRate() {
                return sampleRate;
            }

            public Builder setSampleRate(int sampleRate) {
                this.sampleRate = sampleRate;
                return this;
            }

            public int getSamples() {
                return samples;
            }

            public Builder setSamples(byte samples) {
                this.samples = samples;
                return this;
            }

            public int getChannel() {
                return channel;
            }

            public Builder setChannel(byte channel) {
                this.channel = channel;
                return this;
            }

            public int getFormat() {
                return format;
            }

            public Builder setFormat(int format) {
                this.format = format;
                return this;
            }

            public int getCodec() {
                return codec;
            }

            public Builder setCodec(int codec) {
                this.codec = codec;
                return this;
            }

            public String getStreamName() {
                return streamName;
            }

            public Builder setStreamName(String streamName) {
                this.streamName = streamName;
                return this;
            }

            @SuppressWarnings("MagicConstant")
            @Override
            public Factory build() {
                assert protocol != -1 : "Protocol is required to be set!";
                assert samples != -1 : "Samples is required to be set!";
                return new Factory(protocol, sampleRate, samples, channel, format, codec, streamName);
            }
        }
    }
}
