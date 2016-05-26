package de.factoryfx.model.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface AttributesAware {

    @JsonIgnore
    default Field[] getFields() {
        java.util.List<Field> fields = Stream.of(this.getClass().getFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
        return fields.toArray(new Field[fields.size()]);
    }

    default void visitAttributesDualFlat(Object modelBase, BiConsumer<Attribute<?>, Attribute<?>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute<?>) field.get(this), (Attribute<?>) field.get(modelBase));
                }
                if (fieldValue instanceof AttributesAware) {
                    ((AttributesAware) fieldValue).visitAttributesDualFlat(field.get(modelBase), consumer);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    default void visitAttributesFlat(Consumer<Attribute<?>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute) fieldValue);
                }
                if (fieldValue instanceof AttributesAware) {
                    ((AttributesAware) fieldValue).visitAttributesFlat(consumer);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    default void visitAttributesTripleFlat(Optional<?> modelBase1, Optional<?> modelBase2, TriConsumer<Attribute<?>, Optional<Attribute<?>>, Optional<Attribute<?>>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
//                fields[f].setAccessible(true);
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {

                    Attribute<?> value1 = null;
                    if (modelBase1.isPresent()) {
                        value1 = (Attribute<?>) field.get(modelBase1.get());
                    }
                    Attribute<?> value2 = null;
                    if (modelBase2.isPresent()) {
                        value2 = (Attribute<?>) field.get(modelBase2.get());
                    }
                    consumer.accept((Attribute<?>) field.get(this), Optional.ofNullable(value1), Optional.ofNullable(value2));
                }
                if (fieldValue instanceof AttributesAware) {
                    Optional<Object> value1 = Optional.empty();
                    if (modelBase1.isPresent()) {
                        value1 = Optional.ofNullable(field.get(modelBase1.get()));
                    }
                    Optional<Object> value2 = Optional.empty();
                    if (modelBase2.isPresent()) {
                        value2 = Optional.ofNullable(field.get(modelBase2.get()));
                    }
                    ((AttributesAware) fieldValue).visitAttributesTripleFlat(value1, value2, consumer);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
