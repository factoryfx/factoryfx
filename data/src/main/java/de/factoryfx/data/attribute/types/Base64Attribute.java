package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.Base64;
import java.util.Objects;

/**
 * bytearray as base64 encoded
 */
public class Base64Attribute extends ImmutableValueAttribute<String,Base64Attribute> {
    public Base64Attribute() {
        super(String.class);
    }

    @Override
    public String getDisplayText() {
        if (get()!=null){
            return get();
        }
        return "<empty>";
    }

    @JsonIgnore
    public void set(byte[] bytes){
        set(Base64.getEncoder().encodeToString(bytes));
    }

    @JsonIgnore
    public byte[] getBytes(){
        return Base64.getDecoder().decode(get());
    }

    @Override
    public boolean internal_mergeMatch(String value) {
        return Objects.equals(get(), value);
    }

    private String fileExtension;
    /* ui hint what data should be stored in the attribute*/
    public Base64Attribute fileExtension(String fileExtension){
        this.fileExtension=fileExtension;
        return this;
    }

    public String internal_getFileExtension(){
        return this.fileExtension;
    }




}
