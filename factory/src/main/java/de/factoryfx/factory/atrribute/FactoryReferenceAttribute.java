package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceAttribute<L, F extends FactoryBase<? extends L,?>> extends ReferenceAttribute<F,FactoryReferenceAttribute<L, F>> {

    @JsonCreator
    protected FactoryReferenceAttribute(F value) {
        super(value);
    }


    public FactoryReferenceAttribute(Class<F> clazz) {
        super();
        setup(clazz);
    }

    public FactoryReferenceAttribute() {
        super();
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    @Override
    public FactoryReferenceAttribute<L, F> setupUnsafe(Class clazz){
        return super.setupUnsafe(clazz);
    }

    @Override
    public FactoryReferenceAttribute<L, F> setup(Class<F> clazz){
        return super.setup(clazz);
    }

}
