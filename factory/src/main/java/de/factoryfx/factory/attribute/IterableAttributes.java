package de.factoryfx.factory.attribute;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IterableAttributes {

    @JsonIgnore
    Field[] getFields();

}
