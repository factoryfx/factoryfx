package de.factoryfx.javafx.view.factoryviewmanager;

import javafx.scene.Node;

/**
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> {

    Node init(R serverFactory);

    default Node update(R newFactory){
        return init(newFactory);
    }
}
