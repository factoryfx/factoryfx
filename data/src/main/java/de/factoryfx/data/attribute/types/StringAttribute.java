package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class StringAttribute extends ImmutableValueAttribute<String,StringAttribute> {

    public StringAttribute() {
        super(String.class);
    }

    @JsonCreator
    StringAttribute(String initialValue) {
        super(String.class);
        set(initialValue);
    }

    @JsonIgnore
    private boolean longText=false;
    @JsonIgnore
    private boolean defaultExpanded =false;
    /**hint for data editing, for long text textarea instead of textfield is used*/
    @JsonIgnore
    public StringAttribute longText(){
        longText=true;
        return this;
    }

    @JsonIgnore
    public StringAttribute defaultExpanded(boolean defaultExpanded){
        this.defaultExpanded=defaultExpanded;
        return this;
    }

    public boolean isEmpty(){
        return Strings.isNullOrEmpty(get());
    }

    @JsonIgnore
    public boolean internal_isLongText(){
        return longText;
    }

    @JsonIgnore
    public boolean internal_isDefaultExpanded(){
        return defaultExpanded;
    }

    @Override
    protected StringAttribute createNewEmptyInstance() {

        return new StringAttribute();
    }
}
