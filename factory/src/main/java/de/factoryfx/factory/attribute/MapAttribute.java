package de.factoryfx.factory.attribute;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>,MapAttribute<K, V>> {

    public MapAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        set(FXCollections.observableMap(new TreeMap<>()));

        get().addListener((MapChangeListener<K, V>) change -> {
            for (AttributeChangeListener<ObservableMap<K,V>> listener: listeners){
                listener.changed(MapAttribute.this,get());
            }
        });
    }

    @JsonCreator
    MapAttribute(ObservableMapJacksonAbleWrapper<K, V> map) {
        this((AttributeMetadata)null);
        this.set(map.unwrap());
    }

    @Override
    public String getDisplayText(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (Map.Entry<K,V> item:  get().entrySet()){
            stringBuilder.append(item.getKey()+":"+item.getValue());
            stringBuilder.append(",\n");
        }
        return metadata.labelText.getPreferred(locale)+":\n"+stringBuilder.toString();
    }

}
