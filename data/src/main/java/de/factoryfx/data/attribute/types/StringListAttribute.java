package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringListAttribute extends ValueListAttribute<String> {

    public StringListAttribute(AttributeMetadata attributeMetadata) {
        super(String.class,attributeMetadata,"empty");
    }

    @JsonCreator
    StringListAttribute(ObservableListJacksonAbleWrapper<String> list) {
        super(null,null,null);
        set(list.unwrap());
    }

    public List<String> asUnmodifiableList() {
        return Collections.unmodifiableList(get().stream().collect(Collectors.toList()));
    }

}
