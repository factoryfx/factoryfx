package io.github.factoryfx.factory.atrribute;

import io.github.factoryfx.data.attribute.ReferenceListAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactory;

import java.util.*;

/**
 * Attribute for polymorphic Reference.
 * Usually interface with different implementations
 *
 * @param <L> the base interface/class
 */
public class FactoryPolymorphicReferenceListAttribute<L> extends ReferenceListAttribute<FactoryBase<? extends L,?>,FactoryPolymorphicReferenceListAttribute<L>> {


    public FactoryPolymorphicReferenceListAttribute() {
        super();
    }

    @SafeVarargs
    public FactoryPolymorphicReferenceListAttribute(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses) {
        super();
        setup(liveObjectClass,possibleFactoriesClasses);
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>();
        for(FactoryBase<? extends L,?> item: get()){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    private List<Class<?>> possibleFactoriesClasses;


    /**
     * workaround: if possibleFactoriesClasses has generic parameter the normal setup method doesn't work
     * @param liveObjectClass liveObjectClass
     * @param possibleFactoriesClasses possibleFactoriesClasses
     * @return self
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceListAttribute<L> setupUnsafe(Class liveObjectClass, Class... possibleFactoriesClasses){
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
    @SafeVarargs
    public final FactoryPolymorphicReferenceListAttribute<L> setup(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){
        this.possibleFactoriesClasses=Arrays.asList(possibleFactoriesClasses);
        new FactoryPolymorphicUtil<L>().setup(this,liveObjectClass,()->this.root,possibleFactoriesClasses);
        return this;
    }


    /**
     * intended to be used from code generators
     * @return list of possible classes
     * */
    public List<Class<?>> internal_possibleFactoriesClasses(){
        return possibleFactoriesClasses;
    }


    @SuppressWarnings("unchecked")
    public <T extends FactoryBase> T get(Class<T> clazz) {
        for (FactoryBase<? extends L, ?> item : this.get()) {
            if (item.getClass()==clazz){
                return (T)item;
            }
        }
        return null;
    }

}
