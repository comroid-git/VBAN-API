package de.kaleidox.vban.packet;

import org.intellij.lang.annotations.MagicConstant;

import de.kaleidox.util.interfaces.ByteArray;

import static de.kaleidox.util.Util.appendByteArray;
import static de.kaleidox.util.Util.minSizeArray;
import static de.kaleidox.util.Util.stringToBytesASCII;

public class VBANPacketHead implements ByteArray {
    public final static int SIZE = 28;

    private final byte[] bytes;

    private VBANPacketHead(@MagicConstant(flagsFromClass = Protocol.class) int protocol,
                           @MagicConstant(flagsFromClass = SampleRate.class) int sampleRateIndex,
                           byte samples,
                           byte channel,
                           @MagicConstant(flagsFromClass = Format.class) int format,
                           @MagicConstant(flagsFromClass = Codec.class) int codec,
                           String streamName,
                           int frameCounter) {
        byte[] bytes = new byte[SIZE];

        bytes = appendByteArray(bytes, "VBAN".getBytes());
        bytes = appendByteArray(bytes, (byte) (sampleRateIndex | protocol));
        bytes = appendByteArray(bytes, samples, channel);
        bytes = appendByteArray(bytes, (byte) (format | codec));
        bytes = appendByteArray(bytes, minSizeArray(stringToBytesASCII(streamName), 16));
        bytes = appendByteArray(bytes, (byte) frameCounter);

        this.bytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public static class Factory implements de.kaleidox.util.interfaces.Factory<VBANPacketHead> {
        @MagicConstant(valuesFromClass = Protocol.class)
        private final int protocol;
        @MagicConstant(valuesFromClass = SampleRate.class)
        private final int sampleRate;
        private final byte samples;
        private final byte channel;
        @MagicConstant(valuesFromClass = Format.class)
        private final int format;
        @MagicConstant(valuesFromClass = Codec.class)
        private final int codec;
        private final String streamName;
        private int counter = 0;

        private Factory(int protocol, int sampleRate, byte samples, byte channel, int format, int codec, String streamName) {
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

        public static class Builder implements de.kaleidox.util.interfaces.Builder<Factory> {
            @MagicConstant(valuesFromClass = Protocol.class)
            private int protocol = -1;
            @MagicConstant(valuesFromClass = SampleRate.class)
            private int sampleRate = SampleRate.hz48000;
            private byte samples = -1;
            private byte channel = 2;
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

            public void setProtocol(int protocol) {
                this.protocol = protocol;
            }

            public int getSampleRate() {
                return sampleRate;
            }

            public void setSampleRate(int sampleRate) {
                this.sampleRate = sampleRate;
            }

            public byte getSamples() {
                return samples;
            }

            public void setSamples(byte samples) {
                this.samples = samples;
            }

            public byte getChannel() {
                return channel;
            }

            public void setChannel(byte channel) {
                this.channel = channel;
            }

            public int getFormat() {
                return format;
            }

            public void setFormat(int format) {
                this.format = format;
            }

            public int getCodec() {
                return codec;
            }

            public void setCodec(int codec) {
                this.codec = codec;
            }

            public String getStreamName() {
                return streamName;
            }

            public void setStreamName(String streamName) {
                this.streamName = streamName;
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
