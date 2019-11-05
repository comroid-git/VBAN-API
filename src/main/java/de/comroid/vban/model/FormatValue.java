package de.comroid.vban.model;

import de.comroid.util.model.Bindable;
import de.comroid.util.model.IntEnum;
import de.comroid.vban.VBAN;

import org.jetbrains.annotations.Nullable;

/**
 * Bindable and IntEnum interface for the value that belongs into the format-bit of a packetheader.
 *
 * @param <T> See {@link Bindable} interface.
 *
 * @see de.comroid.vban.VBAN.AudioFormat
 * @see de.comroid.vban.VBAN.Format
 */
public interface FormatValue<T> extends IntEnum, Bindable<T> {
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
     * Gets this as a {@link VBAN.AudioFormat} object.
     *
     * @return This object cast to {@link VBAN.AudioFormat}.
     */
    @Nullable VBAN.AudioFormat asAudioFormat();

    /**
     * Gets this as a {@link VBAN.CommandFormat} object.
     *
     * @return This object cast to {@link VBAN.CommandFormat}.
     */
    @Nullable VBAN.CommandFormat asCommandFormat();

    /**
     * Gets this as a {@link VBAN.Format} object.
     *
     * @return This object cast to {@link VBAN.Format}.
     */
    @Nullable VBAN.Format asFormat();
}
