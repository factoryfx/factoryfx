package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.javafx.data.widget.Widget;

/**
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> extends Widget {

    /**
     * called after initail factory load and after updates to the server
     * @param rootFactory
     */
    void edit(R rootFactory);




}
