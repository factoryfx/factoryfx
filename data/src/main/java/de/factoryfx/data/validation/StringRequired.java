package de.factoryfx.data.validation;


import com.google.common.base.Strings;

public class StringRequired extends ObjectRequired<String> {
    public static final StringRequired VALIDATION = new StringRequired();

    @Override
    public ValidationResult validate(String value) {
        boolean error = Strings.isNullOrEmpty(value);
        return new ValidationResult(error,getText());
    }
}
