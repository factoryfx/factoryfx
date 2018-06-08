package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class StringAttribute extends ImmutableValueAttribute<String,StringAttribute> {

    public StringAttribute() {
        super(String.class);
    }

    @JsonIgnore
    private boolean longText=false;
    @JsonIgnore
    private boolean defaultExpanded =false;

    /**hint for data editing, a textarea is used instead of textfield
     * @return self
     * */
    @JsonIgnore
    public StringAttribute longText(){
        longText=true;
        return this;
    }

    @JsonIgnore
    private boolean htmlText=false;

    /**hint for data editing, wysiwg html editor is used instead of textfield
     * @return self
     * */
    @JsonIgnore
    public StringAttribute htmlText(){
        htmlText=true;
        return this;
    }

    @JsonIgnore
    public StringAttribute defaultExpanded(boolean defaultExpanded){
        this.defaultExpanded=defaultExpanded;
        return this;
    }

    @JsonIgnore
    public boolean isEmpty(){
        return Strings.isNullOrEmpty(get());
    }

    @JsonIgnore
    public boolean internal_isLongText(){
        return longText;
    }

    @JsonIgnore
    public boolean internal_isHtmlText(){
        return htmlText;
    }

    @JsonIgnore
    public boolean internal_isDefaultExpanded(){
        return defaultExpanded;
    }

    @Override
    public String toString() {
        return value;
    }
}
