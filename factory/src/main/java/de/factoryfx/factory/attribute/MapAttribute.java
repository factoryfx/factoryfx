package de.factoryfx.factory.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>> {

    public MapAttribute(AttributeMetadata<ObservableMap<K, V>> attributeMetadata) {
        super(attributeMetadata);
        set(FXCollections.observableMap(new TreeMap<>()));

        get().addListener((MapChangeListener<K, V>) change -> {
            for (AttributeChangeListener<ObservableMap<K,V>> listener: listeners){
                listener.changed(MapAttribute.this,get());
            }
        });
    }

    public MapAttribute(AttributeMetadata<ObservableMap<K, V>> attributeMetadata, Map<K, V> defaultValue) {
        this(attributeMetadata);
        get().putAll(defaultValue);
    }

    @JsonCreator
    public MapAttribute(ObservableMapJacksonAbleWrapper<K, V> map) {
        this((AttributeMetadata<ObservableMap<K, V>>)null);
        this.set(map.unwrap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        ObservableMap<K, V> newMap = (ObservableMap<K, V>) value;
        set(newMap);
    }

    public V get(String key) {
        return get().get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return get().getOrDefault(key, defaultValue);
    }

    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (Map.Entry<K,V> item:  get().entrySet()){
            stringBuilder.append(item.getKey()+":"+item.getValue());
            stringBuilder.append(",\n");
        }
        return metadata.displayName+":\n"+stringBuilder.toString();
    }

}
