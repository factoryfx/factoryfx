package de.factoryfx.development.angularjs.server;

import de.factoryfx.factory.FactoryBase;

public class WebGuiEntity<T extends FactoryBase> {

    public final T factory;
    public final String type;

    public WebGuiEntity(T factory) {
        this.factory = factory;
        this.type = factory.getClass().getName();
    }
}
