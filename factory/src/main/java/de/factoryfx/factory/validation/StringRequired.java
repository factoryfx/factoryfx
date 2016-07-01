package de.factoryfx.factory.validation;

import com.google.common.base.Strings;

public class StringRequired extends ObjectRequired<String> {
    @Override
    public boolean validate(String value) {
        return !Strings.isNullOrEmpty(value);
    }
}
