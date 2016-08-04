package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;

public class StringListAttribute extends ValueListAttribute<String>{

    public StringListAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, String.class,"empty");
    }

    @JsonCreator
    StringListAttribute(ObservableListJacksonAbleWrapper<String> list) {
        super(list);
    }
}
