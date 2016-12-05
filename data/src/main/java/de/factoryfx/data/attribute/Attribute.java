package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;

public abstract class Attribute<T>{
    @JsonIgnore
    public final AttributeMetadata metadata;
    @JsonIgnore
    public final List<Validation<T>> validations = new ArrayList<>();


    @SuppressWarnings("unchecked")
    public <A extends Attribute<T>> A validation(Validation<T> validation){
        this.validations.add(validation);
        return (A)this;
    }

    public Attribute(AttributeMetadata attributeMetadata) {
        this.metadata=attributeMetadata;
    }

    public abstract void collectChildren(Set<Data> allModelEntities);

    public abstract AttributeMergeHelper<?> createMergeHelper();

    @SuppressWarnings("unchecked")
    public <A extends Attribute<T>> A defaultValue(T defaultValue) {
        set(defaultValue);
        return (A)this;
    }

    /*
    see test {{@Link MergerTest#test_dublicate_ids_bug}} why this is needed
    */
    public abstract void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity);

    public abstract T get();

    public abstract void set(T value);


    public abstract void copyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider);

    public abstract void semanticCopyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider);

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = new ArrayList<>();
        for (Validation<T> validation : validations) {
            if (!validation.validate(get())){
                validationErrors.add(new ValidationError(validation.getValidationDescription(),this));
            }
        }
        return validationErrors;
    }


    public abstract void addListener(AttributeChangeListener<T> listener);

    /** remove added Listener or Listener inside WeakAttributeChangeListener*/
    public abstract void removeListener(AttributeChangeListener<T> listener);

    @JsonIgnore
    public abstract String getDisplayText();

    public interface AttributeVisitor{
        void value(Attribute<?> value);
        void reference(ReferenceAttribute<?> reference);
        void referenceList(ReferenceListAttribute<?> referenceList);
    }

    public abstract void visit(AttributeVisitor attributeVisitor);

    public void visit(Consumer<Data> nestedFactoriesVisitor){
        visit(new AttributeVisitor() {
            @Override
            public void value(Attribute<?> value) {

            }

            @Override
            public void reference(ReferenceAttribute<?> reference) {
                reference.getOptional().ifPresent((factory)->nestedFactoriesVisitor.accept(factory));
            }

            @Override
            public void referenceList(ReferenceListAttribute<?> referenceList) {
                referenceList.get().forEach(new Consumer<Data>() {
                    @Override
                    public void accept(Data item) {
                        nestedFactoriesVisitor.accept(item);
                    }
                });
            }
        });
    }

    public abstract AttributeTypeInfo getAttributeType();

    public void prepareUsage(Data root){
        //nothing
    }

    //all elements prepared and root is usable
    public void afterPreparedUsage(Data root){
        //nothing
    }

    public void endUsage() {
        //nothing
    }
}
