package de.factoryfx.data.validation;

import java.util.Optional;
import java.util.regex.Pattern;

import de.factoryfx.data.util.LanguageText;

public class RegexValidation implements Validation<String> {
    private final Pattern pattern;
    public RegexValidation(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public ValidationResult validate(String value) {
        boolean error = Optional.ofNullable(value).map(ss -> pattern.matcher(ss).matches()).orElse(true);
        return new ValidationResult(error,new LanguageText().en("Input match pattern '" + pattern.pattern() + "'"));
    }
}
