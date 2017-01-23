package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;
import de.factoryfx.data.jackson.ObjectValueAttributeDeserializer;
import de.factoryfx.data.jackson.ObjectValueAttributeSerializer;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.NopMergeHelper;

//**
// *special case Attribute to pass object from outside in the application.
// *the ObjectValue ist not serialised or merged
// */
@JsonSerialize(using = ObjectValueAttributeSerializer.class)
@JsonDeserialize(using = ObjectValueAttributeDeserializer.class)
public class ObjectValueAttribute<T> extends ValueAttribute<T> {
    @JsonCreator
    ObjectValueAttribute(T value) {
        super(null,null);
        set(value);
    }

    public ObjectValueAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,null);
    }

    @Override
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new NopMergeHelper<>();
    }

//    @Override
//    @SuppressWarnings("unchecked")
//    public void internal_copyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
//        copyAttribute.set((T) dataCopyProvider.apply(get()));
//    }
}