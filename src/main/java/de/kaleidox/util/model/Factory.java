package de.kaleidox.util.model;

public interface Factory<T> {
    T create();

    int counter();
}
