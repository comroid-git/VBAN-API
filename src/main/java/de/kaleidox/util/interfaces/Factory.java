package de.kaleidox.util.interfaces;

public interface Factory<T> {
    T create();

    int counter();
}
