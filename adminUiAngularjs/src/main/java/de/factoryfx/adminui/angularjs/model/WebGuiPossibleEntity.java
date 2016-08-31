package de.factoryfx.adminui.angularjs.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.factory.FactoryBase;

public class WebGuiPossibleEntity {
    public String displayText;
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public FactoryBase<?,?> factory;

    public WebGuiPossibleEntity(FactoryBase<?,?> factoryBase){
        displayText=factoryBase.getDisplayText();
        factory=factoryBase;
    }
}
