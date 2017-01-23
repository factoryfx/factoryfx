package de.factoryfx.data.attribute;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.jackson.ObservableMapJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>> {
    private final Class<K> keyType;
    private final Class<V> valueType;

    public MapAttribute(AttributeMetadata attributeMetadata, Class<K> keyType, Class<V> valueType) {
        super(attributeMetadata,null);
        this.keyType=keyType;
        this.valueType=valueType;
        set(FXCollections.observableMap(new TreeMap<>()));

        get().addListener((MapChangeListener<K, V>) change -> {
            for (AttributeChangeListener<ObservableMap<K,V>> listener: listeners){
                listener.changed(MapAttribute.this,get());
            }
        });
    }

    @JsonCreator
    MapAttribute(ObservableMapJacksonAbleWrapper<K, V> map) {
        this(null,null,null);
        this.set(map.unwrap());
    }

    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (Map.Entry<K,V> item:  get().entrySet()){
            stringBuilder.append(item.getKey()+":"+item.getValue());
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableMap.class,keyType,valueType, AttributeTypeInfo.AttributeTypeCategory.MAP);
    }

}
