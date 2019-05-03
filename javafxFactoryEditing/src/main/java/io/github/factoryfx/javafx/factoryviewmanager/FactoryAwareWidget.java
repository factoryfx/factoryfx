package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.javafx.widget.Widget;

/**
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> extends Widget {

    /**
     * called after initial factory load and after updates to the server
     * @param rootFactory new rootFactory
     */
    void edit(R rootFactory);




}
