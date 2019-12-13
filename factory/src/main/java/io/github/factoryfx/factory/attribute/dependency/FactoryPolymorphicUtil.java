package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class FactoryPolymorphicUtil<L> {

    @SuppressWarnings("unchecked")
    public void setup(ReferenceBaseAttribute<FactoryBase<? extends L,?>,?,?> attribute, Class<L> liveObjectClass, Supplier<FactoryBase<?,?>> root, Class<? extends PolymorphicFactory<L>>... possibleFactoriesClasses){
        attribute.possibleValueProvider(data -> {
            Set<FactoryBase<? extends L, ?>> result = new HashSet<>();
            for (FactoryBase<?,?> factory: root.get().internal().collectChildrenDeep()){
                if (factory instanceof PolymorphicFactory){
                    if (liveObjectClass.isAssignableFrom(((PolymorphicFactory<L>)factory).getLiveObjectClass())){
                        result.add((FactoryBase<L, ?>) factory);
                    }
                }
            }

            return result;
        });

        //compile time validation doesn't work java generic limitation
        for (Class<? extends PolymorphicFactory<L>> clazz: possibleFactoriesClasses){

            try {
                Constructor<? extends PolymorphicFactory<L>> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                PolymorphicFactory<L> newInstance = constructor.newInstance();

                if (!liveObjectClass.isAssignableFrom(newInstance.getLiveObjectClass())){
                    throw new IllegalArgumentException("class has wrong liveobject: "+clazz);
                }

            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        attribute.newValuesProvider((data,a) -> {
            try {
                ArrayList<FactoryBase<? extends L, ?>> result = new ArrayList<>();
                for (Class<?> clazz: possibleFactoriesClasses){
                    result.add((FactoryBase<L, ?>) clazz.getConstructor().newInstance());
                }
                return result;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
