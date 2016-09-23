package de.factoryfx.adminui.angularjs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.factory.FactoryBase;

public class FactoryTypeInfoWrapper {
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final FactoryBase<?> factory;

    public FactoryTypeInfoWrapper(@JsonProperty("factory")FactoryBase<?> factory) {
        this.factory = factory;
    }


}
