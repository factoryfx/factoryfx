package de.factoryfx.model.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.merge.attribute.AttributeMergeHelper;
import de.factoryfx.model.validation.Validation;
import de.factoryfx.model.validation.ValidationResult;
import javafx.beans.Observable;

public abstract class Attribute<T> implements Observable {

    public final AttributeMetadata<T> metadata;

    public Attribute(AttributeMetadata<T> metadata) {
        this.metadata = metadata;
    }


    public abstract void collectChildren(Set<FactoryBase<?,?>> allModelEntities);

    public abstract AttributeMergeHelper<?> createMergeHelper();

    /*
    see test {{@Link MergerTest#test_dublicate_ids_bug}} why this is needed
    */
    public void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity) {
        //do nothing
    }

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
}
