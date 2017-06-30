package de.factoryfx.factory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.factory.log.FactoryLogEntryEventType;

import java.lang.reflect.ParameterizedType;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class PolymorphicFactoryBase<L,V> extends FactoryBase<L,V> implements PolymorphicFactory<L>{

    public abstract L createImpl();

    @Override
    L create(){
        if (creator!=null){
            return creator.get();
        }
        return loggedAction(FactoryLogEntryEventType.CREATE, this::createImpl);
    }

    @Override
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public Class<L> getLiveObjectClass() {
        return (Class<L>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
