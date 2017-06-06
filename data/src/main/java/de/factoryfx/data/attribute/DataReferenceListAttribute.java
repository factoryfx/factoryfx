package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataReferenceListAttribute<T extends Data> extends ReferenceListAttribute<T,DataReferenceListAttribute<T>> {
    ObservableList<T> list = FXCollections.observableArrayList();

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(Class ,AttributeMetadata)
     * */
    public DataReferenceListAttribute(Class<T> containingFactoryClass, AttributeMetadata attributeMetadata) {
        super(containingFactoryClass,attributeMetadata);
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(AttributeMetadata,Class)
     * */
    @SuppressWarnings("unchecked")
    public DataReferenceListAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata,clazz);
    }

    @JsonCreator
    protected DataReferenceListAttribute() {
        super(null,(AttributeMetadata)null);
    }

    @Override
    public Attribute<List<T>> internal_copy() {
        final DataReferenceListAttribute<T> result = new DataReferenceListAttribute<>(containingFactoryClass, metadata);
        result.set(get());
        return result;
    }

}
