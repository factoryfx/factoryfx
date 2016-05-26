package de.factoryfx.factory.validation;

import java.util.Optional;
import java.util.regex.Pattern;

public class RegexValidation extends SimpleValidation<String> {

    public RegexValidation(Pattern pattern) {
        super(s -> new ValidationResult(Optional.ofNullable(s).map(ss -> pattern.matcher(ss).matches()).orElse(true), "Input does not match pattern '" + pattern.pattern() + "'"), "pattern match");
    }
}
