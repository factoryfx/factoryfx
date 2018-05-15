package de.factoryfx.javafx.factory.view.factoryviewmanager;

import java.util.Optional;

/**
 *
 * @param <R> root
 */
public interface FactoryRootChangeListener<R> {

    void update(Optional<R> previousRoot, R newRoot);
}
