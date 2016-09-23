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
    public LanguageText getValidationDescription() {
        return new LanguageText().en("Input match pattern '" + pattern.pattern() + "'");
    }

    @Override
    public boolean validate(String value) {
        return Optional.ofNullable(value).map(ss -> pattern.matcher(ss).matches()).orElse(true);
    }
}
