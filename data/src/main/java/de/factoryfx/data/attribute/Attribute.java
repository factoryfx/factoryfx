package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;

public abstract class Attribute<T>{
    @JsonIgnore
    public final AttributeMetadata metadata;
    @JsonIgnore
    private final List<Validation<T>> validations = new ArrayList<>();

    public Attribute(AttributeMetadata attributeMetadata) {
        this.metadata=attributeMetadata;
    }

    public abstract void internal_collectChildren(Set<Data> allModelEntities);

    public abstract Attribute<T> internal_copy();

    public abstract boolean internal_match(T value);

    /**
     * merge logic delegate AttributeMergeHelper
     * @return MergeHelper or null if no merging should be executed
     */
    public abstract AttributeMergeHelper<?> internal_createMergeHelper();

    /*
        see test {{@Link MergeTest#test_duplicate_ids_bug}} why this is needed
    */
    public abstract void internal_fixDuplicateObjects(Map<String, Data> idToDataMap);

    public abstract void internal_copyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider);

    @SuppressWarnings("unchecked")
    public boolean internal_match(Attribute<?> attribute) {
        return internal_match((T) attribute.get());
    }

    public abstract void internal_semanticCopyTo(Attribute<T> copyAttribute);

    public List<ValidationError> internal_validate(Data parent) {
        List<ValidationError> validationErrors = new ArrayList<>();
        for (Validation<T> validation : validations) {
            if (!validation.validate(get())){
                validationErrors.add(new ValidationError(validation.getValidationDescription(),this,parent));
            }
        }
        return validationErrors;
    }

    public boolean internal_required() {
        for (Validation<?> validation: validations){
            if (validation instanceof ObjectRequired<?>) {
                return true;
            }
        }
        return false;
    }


    public abstract void internal_visit(AttributeVisitor attributeVisitor);

    public void internal_visit(Consumer<Data> childFactoriesVisitor){
        internal_visit(new AttributeVisitor() {
            @Override
            public void value(Attribute<?> value) {

            }

            @Override
            public void reference(ReferenceAttribute<?> reference) {
                reference.getOptional().ifPresent(childFactoriesVisitor::accept);
            }

            @Override
            public void referenceList(ReferenceListAttribute<?> referenceList) {
                referenceList.get().forEach(childFactoriesVisitor::accept);
            }
        });
    }

    public abstract AttributeTypeInfo internal_getAttributeType();

    public void internal_prepareUsage(Data root){
        //nothing
    }

    /**all elements prepared and root is usable*/
    public void internal_afterPreparedUsage(Data root){
        //nothing
    }

    public void internal_endUsage() {
        //nothing
    }

    public abstract void internal_addListener(AttributeChangeListener<T> listener);

    /** remove added Listener or Listener inside WeakAttributeChangeListener*/
    public abstract void internal_removeListener(AttributeChangeListener<T> listener);

    @SuppressWarnings("unchecked")
    public <A extends Attribute<T>> A defaultValue(T defaultValue) {
        set(defaultValue);
        return (A)this;
    }

    public abstract T get();

    public abstract void set(T value);

    @JsonIgnore
    public abstract String getDisplayText();

    @SuppressWarnings("unchecked")
    public <A extends Attribute<T>> A validation(Validation<T> validation){
        this.validations.add(validation);
        return (A)this;
    }

}
