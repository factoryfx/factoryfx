package de.factoryfx.data.attribute;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<List<T>> {
    private final Class<T> itemType;
    private final T listNewItemEmptyValue;
    private ObservableList<T> observableValue;

    public ValueListAttribute(Class<T> itemType, AttributeMetadata attributeMetadata, T listNewItemEmptyValue) {
        super(attributeMetadata,null);
        this.itemType=itemType;
        this.listNewItemEmptyValue = listNewItemEmptyValue;
        observableValue = FXCollections.observableArrayList();
        value=observableValue;

        observableValue.addListener((ListChangeListener<T>) c -> {
            for (AttributeChangeListener<List<T>> listener: listeners){
                listener.changed(ValueListAttribute.this,get());
            }
        });
    }

    @JsonCreator
    ValueListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        this(null,null,null);
        set(list.unwrap());
    }

    public boolean add(T item){
        return get().add(item);
    }


    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION,listNewItemEmptyValue);
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){//workaround for jackson
            observableValue.clear();
        } else {
            observableValue.setAll(value);
        }
    }

    public List<T> filtered(Predicate<T> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
    }

}
