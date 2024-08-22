package io.github.factoryfx.microservice.common;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckUserResponse {

    public final boolean valid;
    public final Collection<String> permissions;

    @JsonCreator
    public CheckUserResponse(@JsonProperty("valid") boolean valid, @JsonProperty("permissions") Collection<String> permissions) {
        this.valid = valid;
        this.permissions = permissions;
    }

}
