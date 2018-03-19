package de.factoryfx.factory.atrribute;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceBaseAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class FactoryPolymorphicUtil<L> {
    @SuppressWarnings("unchecked")
    public void setup(ReferenceBaseAttribute<FactoryBase<? extends L,?>,?,?> attribute, Class<L> liveObjectClass, Supplier<Data> root, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){
        attribute.possibleValueProvider(data -> {
            Set<FactoryBase<? extends L, ?>> result = new HashSet<>();
            for (Data factory: root.get().internal().collectChildrenDeep()){
                if (factory instanceof PolymorphicFactory){
                    if (liveObjectClass.isAssignableFrom(((PolymorphicFactory)factory).getLiveObjectClass())){
                        result.add((FactoryBase<L, ?>) factory);
                    }
                }
            }

            return result;
        });

        //compile time validation doesn't work java generic limitation
        for (Class<? extends PolymorphicFactory<?>> clazz: possibleFactoriesClasses){
            try {
                PolymorphicFactory<?> newInstance = clazz.newInstance();
                if (!liveObjectClass.isAssignableFrom(((PolymorphicFactory)newInstance).getLiveObjectClass())){
                    throw new IllegalArgumentException("class has wrong liveobject: "+clazz);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        attribute.newValuesProvider(data -> {
            try {
                ArrayList<FactoryBase<? extends L, ?>> result = new ArrayList<>();
                for (Class<?> clazz: possibleFactoriesClasses){
                    result.add((FactoryBase<L, ?>) clazz.newInstance());
                }
                return result;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
