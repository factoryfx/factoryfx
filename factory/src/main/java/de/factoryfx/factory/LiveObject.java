package de.factoryfx.factory;

public interface LiveObject<T> {
    void start();
    void stop();
    /** intent to query runtime state from the application*/
    void accept(T visitor);
}
