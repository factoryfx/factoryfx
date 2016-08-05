package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.MapAttribute;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;

public class StringMapAttribute extends MapAttribute<String,String> {

    public StringMapAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, String.class, String.class);
    }

    @JsonCreator
    StringMapAttribute(ObservableMapJacksonAbleWrapper<String,String> map) {
        super(null,null,null);
        this.set(map.unwrap());
    }
}
