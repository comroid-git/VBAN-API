package de.kaleidox.vban.model;

import de.kaleidox.util.model.Bindable;
import de.kaleidox.util.model.IntEnum;

/**
 * Bindable & IntEnum interface for the value that belongs into the sr-bit of a packetheader.
 *
 * @param <T> See {@link Bindable} interface.
 *
 * @see de.kaleidox.vban.VBAN.SampleRate
 * @see de.kaleidox.vban.VBAN.BitsPerSecond
 */
public interface SRValue<T> extends IntEnum, Bindable<T> {
}
