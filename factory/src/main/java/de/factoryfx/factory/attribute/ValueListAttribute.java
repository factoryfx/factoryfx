package de.factoryfx.factory.attribute;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<ObservableList<T>,ValueListAttribute<T>> {


    public ValueListAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        set(FXCollections.observableArrayList() );

        get().addListener((ListChangeListener<T>) c -> {
            for (AttributeChangeListener<ObservableList<T>> listener: listeners){
                listener.changed(ValueListAttribute.this,get());
            }
        });
    }

    @JsonCreator
    ValueListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        this((AttributeMetadata)null);
        set(list.unwrap());
    }

    public boolean add(T item){
        return get().add(item);
    }


    @Override
    public String getDisplayText(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return metadata.labelText.getPreferred(locale)+":\n"+stringBuilder.toString();
    }

}
