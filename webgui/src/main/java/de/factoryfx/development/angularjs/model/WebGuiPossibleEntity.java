package de.factoryfx.development.angularjs.model;

import de.factoryfx.factory.FactoryBase;

public class WebGuiPossibleEntity {
    public String displayText;
    public String id;

    public WebGuiPossibleEntity(FactoryBase<?,?> factoryBase){
        displayText=factoryBase.getDisplayText();
        id=factoryBase.getId();
    }
}
