package de.factoryfx.factory;

import de.factoryfx.factory.atrribute.*;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FactoryDictionary<T extends FactoryBase<?,?,?>> {
    private static final Map<Class<?>, FactoryDictionary<?>> factoryDictionaries = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static  <F extends FactoryBase<?,?,?>> FactoryDictionary<F> getFactoryDictionary(Class<F> clazz){
        FactoryDictionary<F> result= (FactoryDictionary<F>)factoryDictionaries.get(clazz);
        if (result==null){
            result=new FactoryDictionary<>();
            factoryDictionaries.put(clazz,result);
        }
        return result;
    }

    private BiConsumer<T,Consumer<FactoryBase<?, ?, ?>>> visitChildFactoriesAndViewsFlat;

    public void setVisitChildFactoriesAndViewsFlat(BiConsumer<T,Consumer<FactoryBase<?, ?, ?>>> visitChildFactoriesAndViewsFlat){
        this.visitChildFactoriesAndViewsFlat=visitChildFactoriesAndViewsFlat;
    }

    public void visitChildFactoriesAndViewsFlat(T data, Consumer<FactoryBase<?, ?, ?>> consumer) {
        if (this.visitChildFactoriesAndViewsFlat != null) {
            this.visitChildFactoriesAndViewsFlat.accept(data, consumer);

        } else

            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryReferenceAttribute) {
                    FactoryBase<?, ?, ?> factory = (FactoryBase<?, ?, ?>) attribute.get();
                    if (factory != null) {
                        consumer.accept(factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryReferenceListAttribute) {
                    List<?> factories = ((FactoryReferenceListAttribute<?, ?>) attribute).get();
                    for (Object factory : factories) {
                        consumer.accept((FactoryBase<?, ?, ?>) factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryViewReferenceAttribute) {
                    FactoryBase<?, ?, ?> factory = (FactoryBase<?, ?, ?>) attribute.get();
                    if (factory != null) {
                        consumer.accept(factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryViewListReferenceAttribute) {
                    List<?> factories = ((FactoryViewListReferenceAttribute<?, ?, ?>) attribute).get();
                    for (Object factory : factories) {
                        consumer.accept((FactoryBase<?, ?, ?>) factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryPolymorphicReferenceAttribute) {
                    FactoryBase<?, ?, ?> factory = (FactoryBase<?, ?, ?>) attribute.get();
                    if (factory != null) {
                        consumer.accept(factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryPolymorphicReferenceListAttribute) {
                    ((FactoryPolymorphicReferenceListAttribute<?>) attribute).get().forEach(factory -> {
                        if (factory != null) {
                            consumer.accept((FactoryBase<?, ?, ?>) factory);
                        }
                    });
                    return;
                }
                if (attribute instanceof ParametrizedObjectCreatorAttribute) {
                    FactoryBase<?, ?, ?> factory = (FactoryBase<?, ?, ?>) attribute.get();
                    if (factory != null) {
                        consumer.accept(factory);
                    }
                    return;
                }
            });

    }


}
