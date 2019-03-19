package io.github.factoryfx.data.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;

public class AttributeValidation<T> {
    private final Validation<T> validation;
    private final Attribute[] dependencies;

    public AttributeValidation(Validation<T> validation, Attribute<?,?>... dependencies) {
        this.validation = validation;
        this.dependencies = dependencies;
    }

    @SuppressWarnings("unchecked")
    public Map<Attribute<?,?>,List<ValidationError>> validate(Data data) {
        ValidationResult validationResult = validation.validate((T) data);
        if (validationResult.validationFailed()){
            Map<Attribute<?,?>,List<ValidationError>> result = new HashMap<>();
            for (Attribute<?,?> dependency: dependencies){
                List<ValidationError> validationErrors = result.computeIfAbsent(dependency, k -> new ArrayList<>());
                validationErrors.add(validationResult.createValidationError(dependency,data,""));
            }
            return result;
        }
        return new HashMap<>();
    }

}
