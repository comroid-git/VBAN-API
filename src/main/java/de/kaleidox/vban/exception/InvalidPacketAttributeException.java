package de.kaleidox.vban.exception;

import java.io.IOException;

import de.kaleidox.vban.packet.VBANPacket;

/**
 * Thrown when a {@code byte[]} cannot be decoded to a {@link VBANPacket.Decoded} for any reason.
 */
public class InvalidPacketAttributeException extends IOException {
    /**
     * Constructor
     *
     * @param message Why the bytes cannot be decoded
     */
    public InvalidPacketAttributeException(String message) {
        super(message);
    }
}
