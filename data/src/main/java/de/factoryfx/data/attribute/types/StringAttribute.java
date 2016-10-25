package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    /** for long text texare instead of textfield is used for editing*/
    @JsonIgnore
    public StringAttribute longText(){
        longText=true;
        return this;
    }

    @JsonIgnore
    public boolean isLongText(){
        return longText;
    }
}
