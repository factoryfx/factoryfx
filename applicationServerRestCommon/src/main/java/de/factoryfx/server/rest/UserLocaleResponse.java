package de.factoryfx.server.rest;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLocaleResponse {
    public final Locale locale;

    @JsonCreator
    public UserLocaleResponse(@JsonProperty("valid")Locale locale) {
        this.locale = locale;
    }
}
