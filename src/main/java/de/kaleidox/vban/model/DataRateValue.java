package de.kaleidox.vban.model;

import de.kaleidox.util.model.Bindable;
import de.kaleidox.util.model.IntEnum;
import de.kaleidox.vban.VBAN;

import org.jetbrains.annotations.Nullable;

/**
 * Bindable and IntEnum interface for the value that belongs into the sr-bit of a packetheader.
 *
 * @param <T> See {@link Bindable} interface.
 *
 * @see de.kaleidox.vban.VBAN.SampleRate
 * @see de.kaleidox.vban.VBAN.BitsPerSecond
 */
public interface DataRateValue<T> extends IntEnum, Bindable<T> {
    <R> boolean isType(Class<R> type);

    @Nullable VBAN.SampleRate asSampleRate();

    @Nullable VBAN.BitsPerSecond asBitsPerSecond();
}
