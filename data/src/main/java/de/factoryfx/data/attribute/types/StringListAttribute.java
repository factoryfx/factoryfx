package de.factoryfx.data.attribute.types;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class StringListAttribute extends ValueListAttribute<String,StringListAttribute> {
    @JsonCreator
    public StringListAttribute() {
        super(String.class);
    }


    public List<String> asUnmodifiableList() {
        return Collections.unmodifiableList(get().stream().collect(Collectors.toList()));
    }

}
