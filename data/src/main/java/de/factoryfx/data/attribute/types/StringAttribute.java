package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class StringAttribute extends ValueAttribute<String> {

    public StringAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,String.class);
    }

    @JsonCreator
    StringAttribute(String initialValue) {
        super(null,String.class);
        set(initialValue);
    }

    @JsonIgnore
    private boolean longText=false;
    /**hint for data editing, for long text textarea instead of textfield is used*/
    @JsonIgnore
    public StringAttribute longText(){
        longText=true;
        return this;
    }

    public boolean isEmpty(){
        return Strings.isNullOrEmpty(get());
    }

    @JsonIgnore
    public boolean internal_isLongText(){
        return longText;
    }
}
