package de.factoryfx.factory.atrribute;

import java.util.*;
import java.util.function.Predicate;

import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.AttributeSetupHelper;
import de.factoryfx.factory.FactoryBase;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceListAttribute<L, F extends FactoryBase<? extends L,?,?>> extends  ReferenceListAttribute<F,FactoryReferenceListAttribute<L, F>>{


    public FactoryReferenceListAttribute() {
        super();
    }

    public FactoryReferenceListAttribute(Class<F> clazz) {
        super();
        setup(clazz);
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>();
        for(F item: get()){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    public L instances(Predicate<F> filter){
        Optional<F> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internalFactory().instance()).orElse(null);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> setupUnsafe(Class clazz) {
        return super.setupUnsafe(clazz);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> setup(Class<F> clazz) {
        return super.setup(clazz);
    }

    public void internal_setupWithAttributeSetupHelper(AttributeSetupHelper<?> attributeSetupHelper){
        attributeSetupHelper.setupReferenceListAttribute(this,this.clazz);
    }
}
