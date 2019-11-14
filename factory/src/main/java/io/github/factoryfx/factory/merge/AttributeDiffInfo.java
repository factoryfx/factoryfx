package io.github.factoryfx.factory.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;

import java.util.UUID;

//represent a changed attribute
public class AttributeDiffInfo {
    @JsonProperty
    public final String attributeName;
    @JsonProperty
    public final UUID dataId;

    @JsonCreator
    public AttributeDiffInfo(
            @JsonProperty("attributeName") String attributeName,
            @JsonProperty("parentId") UUID dataId) {
        this.attributeName = attributeName;
        this.dataId = dataId;
    }


    @JsonIgnore
    public String getAttributeDisplayText(FactoryBase<?,?> root){
        Attribute<?,?> attribute = getAttribute(root);
        if (attribute!=null){
            return attribute.getDisplayText();
        }
        return "empty";
    }

    @JsonIgnore
    public Attribute<?,?> getAttribute(FactoryBase<?,?> root){
        FactoryBase<?,?> data = root.internal().collectChildFactoryMap().get(dataId);
        if (data!=null) {
            Attribute<?,?>[] result= new Attribute<?,?>[1];
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attributeVariableName.equals(attributeName)){
                    result[0]=attribute;
                }
            });
            return result[0];
        }
        return null;
    }


    @JsonIgnore
    public boolean isFromFactory(UUID factoryId){
        return dataId.equals(factoryId);
    }

    @JsonIgnore
    public String parentDisplayText(FactoryBase<?,?> root){
        FactoryBase<?,?> data = root.internal().collectChildFactoryMap().get(dataId);
        if (data!=null) {
            return data.internal().getDisplayText();
        }
        return "";
    }


}
