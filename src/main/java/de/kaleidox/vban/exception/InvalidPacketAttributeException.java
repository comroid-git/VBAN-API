package de.kaleidox.vban.exception;

import java.io.IOException;

public class InvalidPacketAttributeException extends IOException {
    public InvalidPacketAttributeException(String message) {
        super(message);
    }
}
