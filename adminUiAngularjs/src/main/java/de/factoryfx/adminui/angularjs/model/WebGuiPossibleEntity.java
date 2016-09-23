package de.factoryfx.adminui.angularjs.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;

public class WebGuiPossibleEntity {
    public String displayText;
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public Data factory;

    public WebGuiPossibleEntity(Data factoryBase){
        displayText=factoryBase.getDisplayText();
        factory=factoryBase;
    }
}
