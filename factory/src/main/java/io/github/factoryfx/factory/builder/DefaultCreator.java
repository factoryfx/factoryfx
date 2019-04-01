package io.github.factoryfx.factory.builder;

import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

public class DefaultCreator<L,F extends FactoryBase<L,R>, R extends FactoryBase<?,R>> implements Function<FactoryContext<R>, F> {
    private final Class<F> clazz;

    public DefaultCreator(Class<F> clazz) {
        this.clazz = clazz;
    }


    @SuppressWarnings("unchecked")
    @Override
    public F apply(FactoryContext<R> context) {
        FactoryMetadata<R,L, F> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);
        F result = factoryMetadata.newInstance();
        factoryMetadata.setAttributeReferenceClasses(result);

        result.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof FactoryReferenceAttribute || attribute instanceof ParametrizedObjectCreatorAttribute){
                FactoryReferenceBaseAttribute factoryReferenceAttribute = (FactoryReferenceBaseAttribute) attribute;
                Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                validateAttributeClass(attributeVariableName, clazz);
                if (factoryReferenceAttribute.internal_required()){
                    context.check(this.clazz, attributeVariableName,clazz);
                }
                FactoryBase factoryBase = context.getUnchecked(clazz);
                factoryReferenceAttribute.set(factoryBase);
            }
            if (attribute instanceof FactoryReferenceListAttribute){
                FactoryReferenceListAttribute factoryReferenceAttribute = (FactoryReferenceListAttribute) attribute;
                Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                validateAttributeClass(attributeVariableName, clazz);
                factoryReferenceAttribute.set(context.getList(clazz));
            }
            if (attribute instanceof FactoryPolymorphicReferenceAttribute){
                FactoryPolymorphicReferenceAttribute factoryReferenceAttribute = (FactoryPolymorphicReferenceAttribute) attribute;
                for (Class<? extends FactoryBase> possibleClazz: (List<Class>)factoryReferenceAttribute.internal_possibleFactoriesClasses()){
                    if (context.anyMatch(possibleClazz)){
                        factoryReferenceAttribute.set(context.get(possibleClazz));
                        break;
                    }
                }
            }
            if (attribute instanceof FactoryPolymorphicReferenceListAttribute){
                FactoryPolymorphicReferenceListAttribute factoryReferenceAttribute = (FactoryPolymorphicReferenceListAttribute) attribute;
                for (Class<? extends FactoryBase> possibleClazz: (List<Class>)factoryReferenceAttribute.internal_possibleFactoriesClasses()){
                    if (context.anyMatch(possibleClazz)){
                        factoryReferenceAttribute.add(context.get(possibleClazz));
                        break;
                    }
                }
            }
        });
        return result;

    }

    protected void validateAttributeClass(String attributeVariableName, Class<? extends FactoryBase> clazz) {
        if (clazz==null){
            throw new IllegalStateException("cant build Factory "+this.clazz+". Attribute: '"+attributeVariableName+"' missing factory clazz info");
        }
    }
}
