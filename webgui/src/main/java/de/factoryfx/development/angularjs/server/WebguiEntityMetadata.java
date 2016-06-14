package de.factoryfx.development.angularjs.server;

import java.util.HashMap;
import java.util.Map;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;

public class WebGuiEntityMetadata {

//    public static class WebGuiAttributeMetadata{
//
//    }

    public String type;

    public Map<String,AttributeMetadata<?>> attributes = new HashMap<>();
    public Map<String,String> attributesTypes = new HashMap<>();



    public WebGuiEntityMetadata(Class<? extends FactoryBase> factoryBaseClass){
        type=factoryBaseClass.getName();
        try {
            FactoryBase<?,?> factoryBase = factoryBaseClass.newInstance();
            factoryBase.visitAttributesFlat((attributeName, attribute) -> {
                attributes.put(attributeName,attribute.metadata);
                attributesTypes.put(attributeName,attribute.getClass().getSimpleName());
            });


        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }



}
