package de.factoryfx.server.angularjs.model.view;

import de.factoryfx.factory.FactoryBase;

public class WebGuiFactoryHeader {
    public final String displayText;
    public final String id;

    public WebGuiFactoryHeader(String displayText, String id) {
        this.displayText = displayText;
        this.id = id;
    }

    public WebGuiFactoryHeader(FactoryBase<?,?> factoryBase) {
        this(factoryBase.internal().getDisplayText(),factoryBase.getId());
    }
}
