package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import de.factoryfx.factory.FactoryBase;

import java.util.Collections;
import java.util.List;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceAttribute<L, F extends FactoryBase<? extends L,?,?>> extends FactoryReferenceBaseAttribute<L,F,FactoryReferenceAttribute<L, F>> {

    @JsonCreator
    @SuppressWarnings("unchecked")
    protected FactoryReferenceAttribute(F value) {
        super(value);
    }

    public FactoryReferenceAttribute(Class<F> clazz) {
        this();
        setup(clazz);
    }

    @SuppressWarnings("unchecked")
    public FactoryReferenceAttribute() {
        super();
    }

    @Override
    public FactoryReferenceAttribute<L, F> setupUnsafe(Class clazz){
        return super.setupUnsafe(clazz);
    }

    @Override
    public FactoryReferenceAttribute<L, F> setup(Class<F> clazz){
        return super.setup(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<F> internal_createNewPossibleValues() {
        if (internal_hasCustomNewValuesProvider()) {
            return super.internal_createNewPossibleValues();
        } else {
            FactoryTreeBuilderBasedAttributeSetup factoryTreeBuilderBasedAttributeSetup = ((FactoryBase) root).utilityFactory().getAttributeSetupHelper();
            if (factoryTreeBuilderBasedAttributeSetup !=null){
                F newFactory = (F) factoryTreeBuilderBasedAttributeSetup.createNewFactory(this.clazz);
                return Collections.singletonList(newFactory);
            }
            return super.internal_createNewPossibleValues();
        }
    }

}
