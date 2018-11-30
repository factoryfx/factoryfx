package de.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class VoidUserAwareRequest extends UserAwareRequest<String> {

    @JsonCreator
    public VoidUserAwareRequest(@JsonProperty("user")String user, @JsonProperty("passwordHash")String passwordHash) {
        super(user, passwordHash, null);
    }

}
