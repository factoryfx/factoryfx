package de.factoryfx.factory.attribute;

import java.util.HashSet;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableSetJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ValueSetAttribute<T> extends ValueAttribute<ObservableSet<T>,ValueSetAttribute<T>> {
    private final Class<T> itemType;
    private final T emptyValue;

    public ValueSetAttribute(AttributeMetadata attributeMetadata, Class<T> itemType, T emptyValue) {
        super(attributeMetadata,null);
        this.itemType = itemType;
        this.emptyValue = emptyValue;
        set(FXCollections.observableSet(new HashSet<>()));

        get().addListener((SetChangeListener<T>) change -> {
            for (AttributeChangeListener<ObservableSet<T>> listener: listeners){
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
    public String getDisplayText(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder("Set (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return metadata.labelText.getPreferred(locale)+":\n"+stringBuilder.toString();
    }

    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(itemType,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION,emptyValue);
    }
}
