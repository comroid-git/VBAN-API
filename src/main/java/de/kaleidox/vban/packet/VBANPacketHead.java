package de.kaleidox.vban.packet;

import org.intellij.lang.annotations.MagicConstant;

import de.kaleidox.util.interfaces.ByteObject;

import static de.kaleidox.util.Util.appendByteArray;
import static de.kaleidox.util.Util.makeByteArray;
import static de.kaleidox.util.Util.minSizeArray;
import static de.kaleidox.util.Util.stringToBytesASCII;

public class VBANPacketHead implements ByteObject {
    public final static int SIZE = 28;

    private final byte[] bytes;

    public VBANPacketHead(@MagicConstant(flagsFromClass = Protocol.class) int protocol,
                          @MagicConstant(flagsFromClass = SampleRate.class) int sampleRateIndex,
                          byte samples,
                          byte channel,
                          @MagicConstant(flagsFromClass = Format.class) int format,
                          @MagicConstant(flagsFromClass = Codec.class) int codec,
                          String streamName,
                          int frameCounter) {
        bytes = new byte[SIZE];
        appendByteArray(bytes, 0, "VBAN".getBytes());
        appendByteArray(bytes, 4, makeByteArray(sampleRateIndex | protocol));
        appendByteArray(bytes, 5, samples);
        appendByteArray(bytes, 6, channel);
        appendByteArray(bytes, 7, makeByteArray(format | codec));
        appendByteArray(bytes, 8, minSizeArray(stringToBytesASCII(streamName), 16));
        appendByteArray(bytes, 24, (byte) frameCounter);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public static class Factory implements de.kaleidox.util.interfaces.Factory<VBANPacketHead> {
        private final int protocol;
        private final int sampleRateIndex;
        private final byte samples;
        private final byte channel;
        private final int format;
        private final int codec;
        private final String streamName;
        private int counter;

        public Factory(String streamName, Builder builder) {
            this.protocol = builder.protocol;
            this.sampleRateIndex = builder.sampleRateIndex;
            this.samples = builder.samples;
            this.channel = builder.channel;
            this.format = builder.format;
            this.codec = builder.codec;
            this.streamName = streamName;

            this.counter = 0;
        }

        @Override
        public VBANPacketHead create() {
            return new VBANPacketHead(protocol, sampleRateIndex, samples, channel, format, codec, streamName, counter++);
        }

        @Override
        public int counter() {
            return counter;
        }
    }

    public static class Builder {
        private int protocol;
        private int sampleRateIndex;
        private byte samples;
        private byte channel;
        private int format;
        private int codec;

        public Builder() {
            protocol = Protocol.AUDIO;
            sampleRateIndex = SampleRate.hz48000;
            samples = (byte) 255;
            channel = (byte) 2;
            format = Format.INT16;
            codec = Codec.PCM;
        }

        public Builder setProtocol(@MagicConstant(flagsFromClass = Protocol.class) int protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setSampleRate(@MagicConstant(flagsFromClass = SampleRate.class) int sampleRateIndex) {
            this.sampleRateIndex = sampleRateIndex;
            return this;
        }

        public Builder setSamples(byte samples) {
            this.samples = samples;
            return this;
        }

        public Builder setChannel(byte channel) {
            this.channel = channel;
            return this;
        }

        public Builder setFormat(@MagicConstant(flagsFromClass = Format.class) int format) {
            this.format = format;
            return this;
        }

        public Builder setCodec(@MagicConstant(flagsFromClass = Codec.class) int codec) {
            this.codec = codec;
            return this;
        }
    }
}
