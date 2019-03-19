package io.github.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyResponse {

    public final String key;

    @JsonCreator
    public KeyResponse(@JsonProperty("valid")String key) {
        this.key = key;
    }


}
