package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactory;

import java.util.*;

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

    @SafeVarargs
    public FactoryPolymorphicReferenceAttribute(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses) {
        super();
        setup(liveObjectClass,possibleFactoriesClasses);
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    private List<Class> possibleFactoriesClasses;


    /**
     * workaround: if possibleFactoriesClasses has generic parameter the normal setup method doesn't work
     * @param liveObjectClass liveObjectClass
     * @param possibleFactoriesClasses possibleFactoriesClasses
     * @return self
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setupUnsafe(Class liveObjectClass, Class... possibleFactoriesClasses){
        this.possibleFactoriesClasses=Arrays.asList(possibleFactoriesClasses);
        for (Class clazz: possibleFactoriesClasses){
            if (!FactoryBase.class.isAssignableFrom(clazz)){
                throw new IllegalArgumentException("parameter must be a factory: "+clazz);
            }
        }
        return setup(liveObjectClass,possibleFactoriesClasses);
    }

    /**
     * setup for select and new value editing
     * @param liveObjectClass type of liveobject
     * @param possibleFactoriesClasses possible factories that crate the liveobject, PolymorphicFactory&lt;L&gt; would be correct but doesn't work
     * @return self
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setup(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){
        this.possibleFactoriesClasses=Arrays.asList(possibleFactoriesClasses);

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


    /**
     * intended to be used from code generators
     * @return list of possible classes
     * */
    public List<Class> internal_possibleFactoriesClasses(){
        return possibleFactoriesClasses;
    }

}
