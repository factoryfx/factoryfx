package de.factoryfx.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.util.LanguageText;

public class DynamicDataAttribute extends Data{
    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
    public final Object value;
    public final Class<? extends Attribute> attributeClass;
    public final LanguageText label;
    public final String name;

    @JsonCreator
    public DynamicDataAttribute(@JsonProperty("value")Object value, @JsonProperty("attributeClass")Class<? extends Attribute> attributeClass, @JsonProperty("label")LanguageText label, @JsonProperty("name")String name) {
        this.value = value;
        this.attributeClass = attributeClass;
        this.label = label;
        this.name = name;
    }

    public Attribute createAttribute()  {
        try {
            return attributeClass.getConstructor(AttributeMetadata.class).newInstance(new AttributeMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
