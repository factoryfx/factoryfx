package io.github.factoryfx.javafx.factoryviewmanager;

import java.util.Optional;

/**
 *
 * @param <R> root
 */
public interface FactoryRootChangeListener<R> {

    void update(Optional<R> previousRoot, R newRoot);
}
