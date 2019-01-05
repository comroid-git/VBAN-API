package de.kaleidox.vban.model;

/**
 * A Factory creates more instances of {@code T} with an internal counter
 * whose value can be acquired with {@link #counter()}.
 *
 * @param <T> Generic type to be created.
 */
public interface Factory<T> {
    /**
     * Creates a new instance of {@code T} and increments the counter.
     *
     * @return A new instance of {@code T}.
     */
    T create();

    /**
     * Returns the internal counter.
     *
     * @return The internal counter.
     */
    int counter();
}
