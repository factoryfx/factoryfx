package de.factoryfx.data.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Map<ValidationError,Attribute<?>>> validate(Data data) {
        if (!validation.validate((T)data)){
            Map<ValidationError,Attribute<?>> result = new HashMap<>();
            for (Attribute<?> dependency: dependencies){
                result.put(new ValidationError(validation.getValidationDescription(),dependency,data),dependency);
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

}
