package de.factoryfx.factory.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

import java.util.function.Function;

public class DefaultCreator<V,F extends FactoryBase<?,V>> implements Function<SimpleFactoryContext<V>, F> {
    private final Class<F> clazz;

    public DefaultCreator(Class<F> clazz) {
        this.clazz = clazz;
    }


    @Override
    public F apply(SimpleFactoryContext<V> context) {
        try {
            F result = clazz.newInstance();
            result.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryReferenceAttribute){
                    FactoryReferenceAttribute factoryReferenceAttribute = (FactoryReferenceAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    FactoryBase factoryBase = context.get(clazz);
                    factoryReferenceAttribute.set(factoryBase);
                }
                if (attribute instanceof FactoryReferenceListAttribute){
                    FactoryReferenceListAttribute factoryReferenceAttribute = (FactoryReferenceListAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    factoryReferenceAttribute.set(context.getList(clazz));
                }
            });
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
