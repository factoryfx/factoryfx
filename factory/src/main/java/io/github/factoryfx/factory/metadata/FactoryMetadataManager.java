package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.FactoryBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FactoryMetadataManager {
    private static final Map<Class<?>, FactoryMetadata<?, ?>> dataReferences = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <R extends FactoryBase<?, R>, F extends FactoryBase<?, R>> FactoryMetadata<R, F> getMetadata(Class<F> clazz) {
        FactoryMetadata<R, F> result = (FactoryMetadata<R, F>) dataReferences.get(clazz);
        if (result == null) {
            result = new FactoryMetadata<>(clazz);
            dataReferences.put(clazz, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static FactoryMetadata getMetadataUnsafe(Class clazz){
        FactoryMetadata result=dataReferences.get(clazz);
        if (result==null){
            result=new FactoryMetadata<>(clazz);
            dataReferences.put(clazz,result);
        }
        return result;
    }
}