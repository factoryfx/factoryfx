package de.factoryfx.data.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class AttributeValidation<T> {
    private final Validation<T> validation;
    private final Attribute[] dependencies;

    public AttributeValidation(Validation<T> validation, Attribute<?>... dependencies) {
        this.validation = validation;
        this.dependencies = dependencies;
    }

    @SuppressWarnings("unchecked")
    public Map<Attribute<?>,List<ValidationError>> validate(Data data) {
        if (!validation.validate((T)data)){
            Map<Attribute<?>,List<ValidationError>> result = new HashMap<>();
            for (Attribute<?> dependency: dependencies){
                List<ValidationError> validationErrors = result.computeIfAbsent(dependency, k -> new ArrayList<>());
                validationErrors.add(new ValidationError(validation.getValidationDescription(),dependency,data));
            }
            return result;
        }
        return new HashMap<>();
    }

}
