package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<ObservableList<T>> {
    private final Class<T> itemType;
    private final T listNewItemEmptyValue;

    public ValueListAttribute(Class<T> itemType, AttributeMetadata attributeMetadata, T listNewItemEmptyValue) {
        super(attributeMetadata,null);
        this.itemType=itemType;
        this.listNewItemEmptyValue = listNewItemEmptyValue;
        set(FXCollections.observableArrayList() );

        get().addListener((ListChangeListener<T>) c -> {
            for (AttributeChangeListener<ObservableList<T>> listener: listeners){
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
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION,listNewItemEmptyValue);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
