package de.kaleidox.vban.model;

import de.kaleidox.util.model.Bindable;
import de.kaleidox.util.model.IntEnum;

/**
 * Bindable & IntEnum interface for the value that belongs into the format-bit of a packetheader.
 *
 * @param <T> See {@link Bindable} interface.
 *
 * @see de.kaleidox.vban.VBAN.AudioFormat
 * @see de.kaleidox.vban.VBAN.Format
 */
public interface FormatValue<T> extends IntEnum, Bindable<T> {
}
