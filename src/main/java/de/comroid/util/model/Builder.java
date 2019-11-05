package de.comroid.util.model;

/**
 * A Builder is used to create one instance of {@code T} with different parameters.
 * The Builder may set all fields to default values, but all fields should be modifyable by methods.
 *
 * @param <T> Generic type to be created.
 */
public interface Builder<T> {
    /**
     * Builds a new instance of {@code T} with the (to this point) specified fields and values.
     *
     * @return A new instance of {@code T}.
     */
    T build();
}
