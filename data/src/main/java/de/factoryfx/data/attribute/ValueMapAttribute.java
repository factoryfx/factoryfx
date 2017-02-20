package de.factoryfx.data.attribute;

import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.jackson.ObservableMapJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ValueMapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>> {
    private final Class<K> keyType;
    private final Class<V> valueType;

    public ValueMapAttribute(AttributeMetadata attributeMetadata, Class<K> keyType, Class<V> valueType) {
        super(attributeMetadata,null);
        this.keyType=keyType;
        this.valueType=valueType;
        set(FXCollections.observableMap(new TreeMap<>()));

        get().addListener((MapChangeListener<K, V>) change -> {
            for (AttributeChangeListener<ObservableMap<K,V>> listener: listeners){
                listener.changed(ValueMapAttribute.this,get());
            }
        });
    }

    @JsonCreator
    ValueMapAttribute(ObservableMapJacksonAbleWrapper<K, V> map) {
        this(null,null,null);
        this.set(map.unwrap());
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get().entrySet(), item -> item.getKey()+":"+item.getValue()).getDisplayText();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableMap.class,keyType,valueType, AttributeTypeInfo.AttributeTypeCategory.MAP);
    }

}
