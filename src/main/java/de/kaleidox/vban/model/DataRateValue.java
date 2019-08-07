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
    /**
     * Conversion method.
     * Used to determine which type this object is.
     *
     * @param type The type to check for.
     * @param <R>  Generic.
     *
     * @return Whether this object is of the given type.
     */
    <R> boolean isType(Class<R> type);

    /**
     * Gets this as a {@link VBAN.SampleRate} object.
     *
     * @return This object cast to {@link VBAN.SampleRate}.
     */
    @Nullable VBAN.SampleRate asSampleRate();

    /**
     * Gets this as a {@link VBAN.BitsPerSecond} object.
     *
     * @return This object cast to {@link VBAN.BitsPerSecond}.
     */
    @Nullable VBAN.BitsPerSecond asBitsPerSecond();
}
