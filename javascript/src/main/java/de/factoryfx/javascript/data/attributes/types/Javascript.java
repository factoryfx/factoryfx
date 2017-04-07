package de.factoryfx.javascript.data.attributes.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Javascript<A> {

    private final String code;
    private final String headerCode;

    @JsonCreator
    public Javascript(String code) {
        this(code,"");
    }

    public Javascript() {
        this("","");
    }

    public Javascript(String code, String headerCode) {
        this.code = code;
        this.headerCode = headerCode;
    }

    public boolean match(Javascript<A> o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        return Objects.equals(code, o.code)
                && Objects.equals(headerCode, o.headerCode);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public Javascript copy() {
        return new Javascript(code);
    }

    public void execute(A api) {
        //TODO: implement
    }


    public Javascript copyWithNewHeaderCode(String headerCode) {
        Javascript copy = new Javascript(code,headerCode);
        return copy;
    }

    public Javascript copyWithNewCode(String newCode) {
        Javascript copy = new Javascript(newCode,headerCode);
        return copy;
    }


    public String getHeaderCode() {
        return headerCode;
    }
}
