package io.github.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//just generic return doesn't work with the proxy client
public class ResponseWorkaround<T> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public T value;

    @SuppressWarnings("unchecked")
    @JsonCreator
    public ResponseWorkaround(@JsonProperty("value") T value) {
        if (value instanceof ResponseWorkaround) {//jackson bug
            this.value = (T) ((ResponseWorkaround)value).value;
        } else {
            this.value = value;
        }
    }
}
