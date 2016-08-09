package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueListAttribute;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;

public class StringListAttribute extends ValueListAttribute<String> {

    public StringListAttribute(AttributeMetadata attributeMetadata) {
        super(String.class,attributeMetadata,"empty");
    }

    @JsonCreator
    StringListAttribute(ObservableListJacksonAbleWrapper<String> list) {
        super(null,null,null);
        set(list.unwrap());
    }
}
