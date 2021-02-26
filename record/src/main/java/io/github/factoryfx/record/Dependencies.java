package io.github.factoryfx.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.util.LanguageText;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

public interface Dependencies<L> {

    default L instance(){
        Class<L> liveClazz =
                (Class<L>)((ParameterizedType)this.getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];


//        for (RecordComponent recordComponent : this.getClass().getRecordComponents()) {
//            System.out.println(recordComponent.getName());
//        }
        try {
            return (L) liveClazz.getConstructors()[0].newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @JsonIgnore
    default List<AttributeAndMetadata> getAttributes() {
        ArrayList<AttributeAndMetadata> attributes = new ArrayList<>(this.getClass().getRecordComponents().length);
        try {
            for (RecordComponent recordComponent : this.getClass().getRecordComponents()) {
                if (recordComponent.getType() == String.class) {
                    StringAttribute stringAttribute = new StringAttribute();

                    stringAttribute.set((String) recordComponent.getAccessor().invoke(this));

                    AttributeMetadata attributeMetadata = new AttributeMetadata(
                            recordComponent.getName(),
                            stringAttribute.getClass(),
                            null,
                            null,
                            null,
                            new LanguageText().en(recordComponent.getName()),
                            stringAttribute.internal_required());
                    attributes.add(new AttributeAndMetadata(stringAttribute,attributeMetadata));
                }
                if (Attribute.class.isAssignableFrom(recordComponent.getType())) {
                    Attribute<?,?> attribute = (Attribute<?,?>) recordComponent.getAccessor().invoke(this);
                    AttributeMetadata attributeMetadata = new AttributeMetadata(
                            recordComponent.getName(),
                            (Class<? extends Attribute<?, ?>>) attribute.getClass(),
                            null,
                            null,
                            null,
                            new LanguageText().en(recordComponent.getName()),
                            attribute.internal_required());
                    attributes.add(new AttributeAndMetadata(attribute,attributeMetadata));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return attributes;
    }

    default Dependencies<L> copy(){
        try {
            var types = new ArrayList<Class<?>>();
            var values = new ArrayList<>();
            for (var component : this.getClass().getRecordComponents()) {
                types.add(component.getType());
                Object value = component.getAccessor().invoke(this);
                if (value instanceof Dependency){
//                    RecordFactory<?, ?, ?> copyFactory = new RecordFactory<>(null);
//                    factory.setId(((Dependency<?,?>)value).get().getId());
                    value= new Dependency<>(null).en("test123");
                }
                values.add(value);
            }

            var canonical = getClass().getDeclaredConstructor(types.toArray(Class[]::new));
            return canonical.newInstance(values.toArray(Object[]::new));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
}
