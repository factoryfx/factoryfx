package de.factoryfx.data.attribute;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.jackson.ObservableSetJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ValueSetAttribute<T> extends ValueAttribute<Set<T>> {
    private final Class<T> itemType;
    private final T emptyValue;

    public ValueSetAttribute(AttributeMetadata attributeMetadata, Class<T> itemType, T emptyValue) {
        super(attributeMetadata,null);
        this.itemType = itemType;
        this.emptyValue = emptyValue;
        final ObservableSet<T> observableSet = FXCollections.observableSet(new HashSet<>());
        value= observableSet;

        observableSet.addListener((SetChangeListener<T>) change -> {
            for (AttributeChangeListener<Set<T>> listener: listeners){
                listener.changed(ValueSetAttribute.this,get());
            }
        });
    }


    @JsonCreator
    ValueSetAttribute(ObservableSetJacksonAbleWrapper<T> setCollection) {
        this(null,null,null);
        set(setCollection.unwrap());
    }

    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("Set (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(ObservableSet.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION,emptyValue);
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(Set<T> value) {
        this.get().clear();
        this.get().addAll(value);
    }
}
