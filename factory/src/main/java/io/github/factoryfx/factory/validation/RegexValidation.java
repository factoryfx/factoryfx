package io.github.factoryfx.factory.validation;

import java.util.Optional;
import java.util.regex.Pattern;

import io.github.factoryfx.factory.util.LanguageText;

/** string match match regex*/
public class RegexValidation implements Validation<String> {
    private final Pattern pattern;
    public RegexValidation(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public ValidationResult validate(String value) {
        boolean error = Optional.ofNullable(value).map(ss -> pattern.matcher(ss).matches()).orElse(true);
        return new ValidationResult(!error,new LanguageText().en("Input match pattern '" + pattern.pattern() + "'"));
    }
}
