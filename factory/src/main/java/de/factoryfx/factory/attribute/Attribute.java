package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.validation.Validation;
import de.factoryfx.factory.validation.ValidationResult;

public abstract class Attribute<T,A extends Attribute<T,A>>{

    public final AttributeMetadata metadata;
    public final List<Validation<T>> validations = new ArrayList<>();

    public A validation(Validation<T> validation){
        this.validations.add(validation);
        return (A)this;
    }

    public Attribute(AttributeMetadata attributeMetadata) {
        this.metadata=attributeMetadata;
    }

    public abstract void collectChildren(Set<FactoryBase<?,?>> allModelEntities);

    public abstract AttributeMergeHelper<?> createMergeHelper();

    public A defaultValue(T defaultValue) {
        set(defaultValue);
        return (A)this;
    }

    /*
    see test {{@Link MergerTest#test_dublicate_ids_bug}} why this is needed
    */
    public abstract void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity);

    public abstract T get();

    public abstract void set(T value);

    public List<ValidationResult> validate() {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (Validation<T> validation : validations) {
            validationResults.add(validation.validate(get()));
        }
        return validationResults;
    }


    public abstract void addListener(AttributeChangeListener<T> listener);

    public abstract void removeListener(AttributeChangeListener<T> listener);

    @JsonIgnore
    public abstract String getDisplayText(Locale locale);

    public interface AttributeVisitor{
        void value(Attribute<?,?> value);
        void reference(ReferenceAttribute<?> reference);
        void referenceList(ReferenceListAttribute<?> referenceList);
    }

    public abstract void visit(AttributeVisitor attributeVisitor);

    public void visit(Consumer<FactoryBase<?,?>> nestedFactoriesVisitor){
        visit(new AttributeVisitor() {
            @Override
            public void value(Attribute<?,?> value) {

            }

            @Override
            public void reference(ReferenceAttribute<?> reference) {
                reference.getOptional().ifPresent((factory)->nestedFactoriesVisitor.accept(factory));
            }

            @Override
            public void referenceList(ReferenceListAttribute<?> referenceList) {
                referenceList.get().forEach(new Consumer<FactoryBase<?,?>>() {
                    @Override
                    public void accept(FactoryBase<?,?> item) {
                        nestedFactoriesVisitor.accept(item);
                    }
                });
            }
        });
    }
}
