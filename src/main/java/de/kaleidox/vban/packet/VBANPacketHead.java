package de.kaleidox.vban.packet;

import de.kaleidox.util.model.ByteArray;
import de.kaleidox.vban.VBAN.AudioFormat;
import de.kaleidox.vban.VBAN.BitsPerSecond;
import de.kaleidox.vban.VBAN.Codec;
import de.kaleidox.vban.VBAN.Format;
import de.kaleidox.vban.VBAN.Protocol;
import de.kaleidox.vban.VBAN.SampleRate;
import de.kaleidox.vban.model.AudioPacket;
import de.kaleidox.vban.model.DataRateValue;
import de.kaleidox.vban.model.FormatValue;

import org.intellij.lang.annotations.MagicConstant;

import static de.kaleidox.vban.Util.appendByteArray;
import static de.kaleidox.vban.Util.checkRange;
import static de.kaleidox.vban.Util.intToByteArray;
import static de.kaleidox.vban.Util.stringToBytesASCII;
import static de.kaleidox.vban.Util.trimArray;
import static de.kaleidox.vban.packet.VBANPacketHead.Factory.builder;

public class VBANPacketHead<T> implements ByteArray {
    public final static int SIZE = 28;

    private final byte[] bytes;

    private VBANPacketHead(int protocol,
                           int sampleRateIndex,
                           int samples,
                           int channel,
                           int format,
                           int codec,
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
        this.bytes = appendByteArray(bytes, intToByteArray(frameCounter, 4));
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Creates a Factory instance with the default properties for the specified Protocol.
     *
     * @param forProtocol The protocol to use standards for.
     * @param <T>         Type-variable for the VBAN-Stream.
     *
     * @return A new Factory instance.
     * @throws UnsupportedOperationException If the Protocol is one of {@code [AUDIO, SERIAL, SERVICE]}.
     */
    public static <T> Factory<T> defaultFactory(Protocol<T> forProtocol) throws UnsupportedOperationException {
        return builder(forProtocol).build();
    }

    /**
     * Creates a Factory with the default settings for audio streams.
     *
     * @param channel The number of channels to be supported by the stream.
     *
     * @return A new Factory instance.
     * @throws UnsupportedOperationException Always, because Audio communication is not implemented yet.
     * @deprecated Use {@link #defaultFactory(Protocol)} instead.
     */
    @Deprecated
    public static Factory<AudioPacket> defaultAudioProtocolFactory(int channel) throws UnsupportedOperationException {
        return builder(Protocol.AUDIO).build();
    }

    /**
     * Creates a Factory with the default settings for text streams.
     *
     * @return A new Factory instance.
     * @deprecated Use {@link #defaultFactory(Protocol)} instead.
     */
    @Deprecated
    public static Factory<String> defaultTextProtocolFactory() {
        return builder(Protocol.TEXT).build();
    }

    /**
     * Creates a Factory with the default settings for serial streams.
     *
     * @return A new Factory instance.
     * @deprecated Use {@link #defaultFactory(Protocol)} instead.
     */
    @Deprecated
    public static Factory<CharSequence> defaultSerialProtocolFactory() {
        return builder(Protocol.SERIAL).build();
    }

    /**
     * Creates a Factory with the default settings for service streams.
     *
     * @return A new Factory instance.
     * @throws UnsupportedOperationException Always, because Service communication is currently unsupported.
     * @deprecated Use {@link #defaultFactory(Protocol)} instead.
     */
    @Deprecated
    public static Factory<ByteArray> defaultServiceProtocolFactory() throws UnsupportedOperationException {
        return builder(Protocol.SERVICE).build();
    }

    public static class Factory<T> implements de.kaleidox.util.model.Factory<VBANPacketHead<T>> {
        private final int protocol;
        private final int sampleRate;
        private final int samples;
        private final int channel;
        private final int format;
        private final int codec;
        private final String streamName;
        private int counter;

        private Factory(Protocol<T> protocol,
                        DataRateValue<T> sampleRate,
                        int samples,
                        int channel,
                        FormatValue<T> format,
                        int codec,
                        String streamName) {
            this.protocol = protocol.getValue();
            this.sampleRate = sampleRate.getValue();
            this.samples = samples;
            this.channel = channel;
            this.format = format.getValue();
            this.codec = codec;
            this.streamName = streamName;

            counter = 0;
        }

        @Override
        public synchronized VBANPacketHead<T> create() {
            return new VBANPacketHead<>(protocol, sampleRate, samples, channel, format, codec, streamName, counter++);
        }

        @Override
        public synchronized int counter() {
            return counter;
        }

        /**
         * Creates a new Builder with the default properties pre-set for the specified protocol.
         *
         * @param protocol The protocol to create the Builder for.
         * @param <T>      Type-Variable for the stream type.
         *
         * @return A new builder for the given protocol.
         * @throws UnsupportedOperationException If the protocol is {@link Protocol#SERVICE}.
         */
        public static <T> Builder<T> builder(Protocol<T> protocol) throws UnsupportedOperationException {
            return new Builder<>(protocol);
        }

        public static class Builder<T> implements de.kaleidox.util.model.Builder<Factory<T>> {
            private final Protocol<T> protocol;
            private DataRateValue<T> sampleRate;
            private int samples;
            private int channel;
            private FormatValue<T> format;
            @MagicConstant(valuesFromClass = Codec.class)
            private int codec = Codec.PCM;
            private String streamName = null;

            /*
            Suppress ConstantConditions because, while the MIDI branch breaks away due to Serial communication not
            being implemented yet, the IF in the Text communication branch will always be 'false'
             */
            @SuppressWarnings({"unchecked", "ConstantConditions"})
            private Builder(Protocol<T> protocol) throws UnsupportedOperationException {
                this.protocol = protocol;

                switch (protocol.getValue()) {
                    case 0x00:
                        sampleRate = (DataRateValue<T>) SampleRate.Hz48000;
                        samples = 255;
                        channel = 2;
                        format = (FormatValue<T>) AudioFormat.INT16;
                        streamName = "Stream1";
                        break;
                    case 0x20:
                        streamName = "MIDI1";
                        break;
                    case 0x40:
                        sampleRate = (DataRateValue<T>) BitsPerSecond.Bps256000;
                        samples = 0;
                        channel = 0;
                        format = (FormatValue<T>) Format.BYTE8;
                        // if because we are in a shared branch
                        if (streamName == null) streamName = "Command1";
                        return;
                    case 0x60:
                        break;
                    default:
                        throw new AssertionError("Unknown Protocol: " + protocol);
                }

                throw new UnsupportedOperationException("Unsupported Protocol: " + protocol);
            }

            public Protocol<T> getProtocol() {
                return protocol;
            }

            public DataRateValue<T> getSampleRate() {
                return sampleRate;
            }

            public Builder setSRValue(DataRateValue<T> sampleRate) {
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

            public FormatValue<T> getFormat() {
                return format;
            }

            public Builder setFormatValue(FormatValue<T> format) {
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

            @Override
            public Factory<T> build() {
                assert protocol != null : "No protocol defined!";

                return new Factory<>(protocol, sampleRate, samples, channel, format, codec, streamName);
            }
        }
    }
}
