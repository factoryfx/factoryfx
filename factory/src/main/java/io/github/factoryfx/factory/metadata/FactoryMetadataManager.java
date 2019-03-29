package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class FactoryMetadataManager {
    private static final Map<Class<?>, FactoryMetadata<?,?>> dataReferences = new ConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    public static <R extends FactoryBase<?,R>,T extends FactoryBase<?,R>> FactoryMetadata<R,T> getMetadata(Class<T> clazz){
        FactoryMetadata<R,T> result=(FactoryMetadata<R,T>)dataReferences.get(clazz);
        if (result==null){
            result=new FactoryMetadata<>(clazz);
            dataReferences.put(clazz,result);
        }
        return result;
    }
}
