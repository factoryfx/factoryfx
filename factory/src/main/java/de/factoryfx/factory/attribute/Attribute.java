package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.validation.Validation;
import de.factoryfx.factory.validation.ValidationResult;

public abstract class Attribute<T>{

    public final AttributeMetadata<T> metadata;

    public Attribute(AttributeMetadata<T> metadata) {
        this.metadata = metadata;
    }


    public abstract void collectChildren(Set<FactoryBase<?,?>> allModelEntities);

    public abstract AttributeMergeHelper<?> createMergeHelper();

    /*
    see test {{@Link MergerTest#test_dublicate_ids_bug}} why this is needed
    */
    public abstract void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity);

    public abstract T get();

    public abstract void set(T value);

    public abstract void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool);

    public List<ValidationResult> validate() {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (Validation<T> validation : metadata.validations) {
            validationResults.add(validation.validate(get()));
        }
        return validationResults;
    }


    public abstract void addListener(AttributeChangeListener<T> listener);

    public abstract void removeListener(AttributeChangeListener<T> listener);

    @JsonIgnore
    public abstract String getDisplayText();
}
