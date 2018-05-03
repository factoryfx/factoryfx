package de.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAwareRequest<T> {

    public final String user;
    public final String passwordHash;
    public final T request;

    @JsonCreator
    public UserAwareRequest(@JsonProperty("user")String user, @JsonProperty("passwordHash")String passwordHash, @JsonProperty("request")T request) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.request = request;
    }
}
