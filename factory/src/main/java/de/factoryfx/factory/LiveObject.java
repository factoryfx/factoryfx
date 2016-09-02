package de.factoryfx.factory;

public interface LiveObject<T> {
    default void start(){};
    default void stop(){};
    /** intent to query runtime state from the application*/
    default void accept(T visitor){};
}
