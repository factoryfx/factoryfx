package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class FactoryPolymorphicUtil<R extends FactoryBase<?,R>,L> {

    @SuppressWarnings("unchecked")
    public void setup(ReferenceBaseAttribute<R,FactoryBase<? extends L,R>,?,?> attribute, Class<L> liveObjectClass, Supplier<R> root, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){
        attribute.possibleValueProvider(data -> {
            Set<FactoryBase<? extends L, R>> result = new HashSet<>();
            for (FactoryBase<?,R> factory: root.get().internal().collectChildrenDeep()){
                if (factory instanceof PolymorphicFactory){
                    if (liveObjectClass.isAssignableFrom(((PolymorphicFactory)factory).getLiveObjectClass())){
                        result.add((FactoryBase<L, R>) factory);
                    }
                }
            }

            return result;
        });

        //compile time validation doesn't work java generic limitation
        for (Class<? extends PolymorphicFactory<?>> clazz: possibleFactoriesClasses){

            try {
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                PolymorphicFactory<?> newInstance = (PolymorphicFactory<?>) constructor.newInstance(new Object[0]);

                if (!liveObjectClass.isAssignableFrom(((PolymorphicFactory)newInstance).getLiveObjectClass())){
                    throw new IllegalArgumentException("class has wrong liveobject: "+clazz);
                }

            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        attribute.newValuesProvider((data,a) -> {
            try {
                ArrayList<FactoryBase<? extends L, R>> result = new ArrayList<>();
                for (Class<?> clazz: possibleFactoriesClasses){
                    result.add((FactoryBase<L, R>) clazz.getConstructor().newInstance());
                }
                return result;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
