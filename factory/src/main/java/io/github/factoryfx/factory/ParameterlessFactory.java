package io.github.factoryfx.factory;

import com.fasterxml.jackson.annotation.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import java.lang.reflect.InvocationTargetException;


/**
 * Utility factory for a liveobject that has no parameter.
 * e.g. setting a ExceptionMapper class in jersey
 * */
public class ParameterlessFactory<L,R extends FactoryBase<?,R>> extends SimpleFactoryBase<L,R> {
    @JsonProperty
    public Class<? extends L> clazz;

    static {
        setup();
    }

    @SuppressWarnings("unchecked")
    private static void setup(){
        FactoryMetadataManager.getMetadataUnsafe(ParameterlessFactory.class).setNewCopyInstanceSupplier(paramterlessFactory -> ((ParameterlessFactory)paramterlessFactory).copy());
    }

    @Override
    protected L createImpl() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private ParameterlessFactory<L,R> copy(){
        ParameterlessFactory<L, R> copy = new ParameterlessFactory<>();
        copy.clazz=clazz;
        return copy;
    }

    public ParameterlessFactory<L,R> withLiveClass(Class<? extends L> clazz){
        this.clazz=clazz;
        return this;
    }

    public static <L,R extends FactoryBase<?,R>> ParameterlessFactory<L,R> create(Class<? extends L> clazz){
        ParameterlessFactory<L, R> result = new ParameterlessFactory<>();
        result.withLiveClass(clazz);
        return result;
    }

}
