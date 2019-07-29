package io.github.factoryfx.factory;

import com.fasterxml.jackson.annotation.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import java.lang.reflect.InvocationTargetException;


/**
 * Utility factory for a liveobject that has no attributes .
 * e.g. setting a ExceptionMapper,ObjectMapper class in jersey
 * */
public class AttributelessFactory<L,R extends FactoryBase<?,R>> extends SimpleFactoryBase<L,R> {
    @JsonProperty
    public Class<? extends L> clazz;

    static {
        setup();
    }

    @SuppressWarnings("unchecked")
    private static void setup(){
        FactoryMetadataManager.getMetadataUnsafe(AttributelessFactory.class).setNewCopyInstanceSupplier(paramterlessFactory -> ((AttributelessFactory)paramterlessFactory).copy());
    }

    @Override
    protected L createImpl() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private AttributelessFactory<L,R> copy(){
        AttributelessFactory<L, R> copy = new AttributelessFactory<>();
        copy.clazz=clazz;
        return copy;
    }

    public AttributelessFactory<L,R> withLiveClass(Class<? extends L> clazz){
        this.clazz=clazz;
        return this;
    }

    public static <L,R extends FactoryBase<?,R>> AttributelessFactory<L,R> create(Class<? extends L> clazz){
        AttributelessFactory<L, R> result = new AttributelessFactory<>();
        result.withLiveClass(clazz);
        return result;
    }

}
