package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;

public class StringMapAttribute extends MapAttribute<String,String>{

    public StringMapAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, String.class, String.class);
    }

    @JsonCreator
    StringMapAttribute(ObservableMapJacksonAbleWrapper<String,String> map) {
        super(map);
    }
}
