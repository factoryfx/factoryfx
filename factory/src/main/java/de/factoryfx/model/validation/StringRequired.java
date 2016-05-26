package de.factoryfx.model.validation;

import com.google.common.base.Strings;

public class StringRequired extends SimpleValidation<String> {
    public StringRequired() {
        super(s -> new ValidationResult(!Strings.isNullOrEmpty(s), "required parameter"), "required parameter");
    }
}
