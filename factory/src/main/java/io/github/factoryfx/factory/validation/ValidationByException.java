package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.util.LanguageText;

public class ValidationByException<T> implements Validation<T> {
    private final ValidatingConsumer<T> function;
    private final LanguageText languageText;

    public interface ValidatingConsumer<T> {
        void consume(T t) throws Exception;
    }

    public ValidationByException(ValidatingConsumer<T> consumer, LanguageText languageText) {
        this.function = consumer;
        this.languageText = languageText;
    }

    public ValidationByException(ValidatingConsumer<T> consumer, String message) {
        this(consumer, new LanguageText(message));
    }

    public ValidationByException(ValidatingConsumer<T> consumer) {
        this(consumer, (LanguageText) null);
    }

    @Override
    public ValidationResult validate(T value) {
        try {
            function.consume(value);
            return ValidationResult.OK;
        } catch (Exception e) {
            return new ValidationResult(true, languageText == null ? new LanguageText(e.getMessage()) : languageText);
        }
    }
}
