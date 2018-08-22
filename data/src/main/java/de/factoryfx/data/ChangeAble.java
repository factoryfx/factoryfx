package de.factoryfx.data;

import java.util.function.Consumer;

public interface ChangeAble<T> {

    T get();

    void afterModify();

    default void apply(Consumer<T> consumer) {
        consumer.accept(get());
        afterModify();
    }

}
