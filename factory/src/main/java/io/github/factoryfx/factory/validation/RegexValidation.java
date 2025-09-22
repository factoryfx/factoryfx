package io.github.factoryfx.factory.validation;

import java.util.Optional;
import java.util.regex.Pattern;

import io.github.factoryfx.factory.util.LanguageText;

/** string match match regex */
public class RegexValidation implements Validation<String> {
    private final Pattern pattern;
    private final boolean matchesFull;

    public RegexValidation(Pattern pattern, boolean matchesFull) {
        this.matchesFull = matchesFull;
        this.pattern = pattern;
    }

    public RegexValidation(Pattern pattern) {
        this(pattern, true);
    }

    @Override
    public ValidationResult validate(String value) {
        return Optional.ofNullable(value)
                       .map(pattern::matcher)
                       .map(mm -> matchesFull ? mm.matches() : mm.find())
                       .filter(found -> !found)
                       .map(b -> new ValidationResult(true, new LanguageText().en("Input match pattern '" + pattern.pattern() + "'")))
                       .orElse(ValidationResult.OK);
    }
}
