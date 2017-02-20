package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueMapAttribute;
import de.factoryfx.data.jackson.ObservableMapJacksonAbleWrapper;

public class StringMapAttribute extends ValueMapAttribute<String,String> {

    public StringMapAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, String.class, String.class);
    }

    @JsonCreator
    StringMapAttribute(ObservableMapJacksonAbleWrapper<String,String> map) {
        super(null,null,null);
        this.set(map.unwrap());
    }
}
