package de.factoryfx.data.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;

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
