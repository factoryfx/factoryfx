package de.factoryfx.model.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.factoryfx.model.validation.Validation;

public class AttributeMetadata<T> {
    public final String displayName;
    public final List<Validation<T>> validations = new ArrayList<>();

    @SafeVarargs
    public AttributeMetadata(String displayName, Validation<T>... validation) {
        this(displayName, Arrays.asList(validation));
    }

    public AttributeMetadata(String displayName, List<Validation<T>> validation) {
        this.displayName = displayName;
        this.validations.addAll(validation);
    }

    public AttributeMetadata(String displayName) {
        this.displayName = displayName;
    }
}
