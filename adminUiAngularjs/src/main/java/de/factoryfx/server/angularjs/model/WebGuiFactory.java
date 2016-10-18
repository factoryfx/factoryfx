package de.factoryfx.server.angularjs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;
import de.factoryfx.factory.FactoryBase;

public class WebGuiFactory {

    public static class PathElement{
        public final String id;
        public final String displayText;

        public PathElement(@JsonProperty("id")String id, @JsonProperty("displayText")String displayText) {
            this.id = id;
            this.displayText = displayText;
        }
    }


    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public FactoryBase<?,?> factory;
    public String type;
    public String displayText;
    public Map<String,String> nestedFactoriesDisplayText = new HashMap<>();

    public List<PathElement> path=new ArrayList<>();

    public WebGuiFactory(FactoryBase<?,?> factory, FactoryBase<?,?> root) {
        this.factory = factory.copyOneLevelDeep();
        this.type = factory.getClass().getName();

        factory.visitAttributesFlat((attributeName, attribute) -> {
            attribute.visit(factoryBase1 -> {
                nestedFactoriesDisplayText.put(factoryBase1.getId().toString(), factoryBase1.getDisplayText());
            });
        });

        displayText=factory.getDisplayText();

        for (Data factoryBase: root.getPathTo(factory)){
            path.add(new PathElement(factoryBase.getId().toString(),factoryBase.getDisplayText()));
        }
    }

    public WebGuiFactory() {
        //for jackson

    }
}
