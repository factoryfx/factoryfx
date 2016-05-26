package de.factoryfx.model.validation;

import java.util.Optional;

public class ValidationResult {
    private final boolean valid;
    private final Optional<String> validationErrorDescription;

    public ValidationResult(boolean valid, String validationErrorDescription) {
        this.valid = valid;
        this.validationErrorDescription = Optional.of(validationErrorDescription);
    }

    public ValidationResult() {
        this.valid = true;
        this.validationErrorDescription = Optional.empty();
    }

    public Optional<String> getValidationErrorDescription() {
        if (valid) {
            return Optional.empty();
        }
        return validationErrorDescription;
    }

    public boolean isValid() {
        return valid;
    }
}
