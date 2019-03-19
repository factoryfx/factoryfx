package io.github.factoryfx.microservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


public class UserAwareRequest<T> {

    public final String user;
    public final String passwordHash;
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final T request;

    @JsonCreator
    public UserAwareRequest(@JsonProperty("user")String user, @JsonProperty("passwordHash")String passwordHash, @JsonProperty("request")T request) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.request = request;
    }
}
