package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Attribute for polymorphic Reference.
 * Usually interface with different implementations
 *
 * @param <L> the base interface/class
 */
public class FactoryPolymorphicReferenceAttribute<L> extends ReferenceAttribute<FactoryBase<L,?>,FactoryPolymorphicReferenceAttribute<L>> {


    @JsonCreator
    protected FactoryPolymorphicReferenceAttribute(FactoryBase<L,?> value) {
        super(value);
    }

    public FactoryPolymorphicReferenceAttribute() {
        super();
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    /**workaround: if possibleFactoriesClasses has generic parameter the normal setup method doesn't work */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setupUnsafe(Class liveObjectClass, Class... possibleFactoriesClasses){
        for (Class clazz: possibleFactoriesClasses){
            if (!FactoryBase.class.isAssignableFrom(clazz)){
                throw new IllegalArgumentException("parameter must be a factory: "+clazz);
            }
        }
        return setup(liveObjectClass,possibleFactoriesClasses);
    }

    /**
     * setup for select and new value editing
     * @param liveObjectClass
     * @param possibleFactoriesClasses
     * @return
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setup(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){/*PolymorphicFactory<L> would be correct but doesn't work*/
        this.possibleValueProvider(data -> {
            Set<FactoryBase<L, ?>> result = new HashSet<>();
            for (Data factory: root.internal().collectChildrenDeep()){
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

        this.newValuesProvider(data -> {
            try {
                ArrayList<FactoryBase<L, ?>> result = new ArrayList<>();
                for (Class<?> clazz: possibleFactoriesClasses){
                    result.add((FactoryBase<L, ?>) clazz.newInstance());
                }
                return result;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }



}
