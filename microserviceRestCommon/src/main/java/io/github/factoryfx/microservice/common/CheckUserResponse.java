package io.github.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckUserResponse {

    public final boolean valid;

    @JsonCreator
    public CheckUserResponse(@JsonProperty("valid")boolean valid) {
        this.valid = valid;
    }


}
