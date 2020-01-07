package io.github.factoryfx.factory.builder;

import java.util.function.Function;

import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

public class DefaultCreator<F extends FactoryBase<?,R>, R extends FactoryBase<?,R>> implements Function<FactoryContext<R>, F> {
    private final Class<F> clazz;

    public DefaultCreator(Class<F> clazz) {
        this.clazz = clazz;
    }


    @SuppressWarnings("unchecked")
    @Override
    public F apply(FactoryContext<R> context) {
        FactoryMetadata<R, F> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);
        F result = factoryMetadata.newInstance();


        result.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            if (attribute instanceof FactoryPolymorphicAttribute){
                FactoryPolymorphicAttribute factoryPolymorphicAttribute = (FactoryPolymorphicAttribute) attribute;
                if (attributeMetadata.liveObjectClass!=null){
                    for (FactoryBase<?, R> factory : context.getListFromLiveObjectClass(attributeMetadata.liveObjectClass,clazz)) {
                        factoryPolymorphicAttribute.set(factory);
                        break;
                    }
                }
                //TODO multiple factories seems wrong, throw exception to enforce for uniqueness?
                return;
            }
            if (attribute instanceof FactoryPolymorphicListAttribute){
                FactoryPolymorphicListAttribute factoryPolymorphicListAttribute = (FactoryPolymorphicListAttribute) attribute;
                if (attributeMetadata.liveObjectClass!=null){
                    for (FactoryBase<?, R> factory : context.getListFromLiveObjectClass(attributeMetadata.liveObjectClass,clazz)) {
                        factoryPolymorphicListAttribute.add(factory);
                        break;
                    }
                }
                return;
            }

            if (attribute instanceof FactoryBaseAttribute ){
                FactoryBaseAttribute factoryBaseAttribute = (FactoryBaseAttribute) attribute;
                Class<? extends FactoryBase<?,?>> clazz = attributeMetadata.referenceClass;
                validateAttributeClass(attributeMetadata.attributeVariableName, clazz);
                FactoryBase<?,?> factoryBase = context.getUnchecked(clazz);
                factoryBaseAttribute.set(factoryBase);
                if (factoryBaseAttribute.internal_required()){
                    if (factoryBaseAttribute.get()==null){
                        throw new IllegalStateException(
                                "\nbuilder missing Factory: "+attributeMetadata.liveObjectClass+"\n"+
                                        "required in: "+clazz+"\n"+
                                        "from attribute: "+attributeMetadata.attributeVariableName
                        );
                    }
                }
                return;
            }
            if (attribute instanceof FactoryListAttribute){
                FactoryListAttribute factoryListAttribute = (FactoryListAttribute) attribute;
                Class<? extends FactoryBase> clazz = attributeMetadata.referenceClass;
                validateAttributeClass(attributeMetadata.attributeVariableName, clazz);
                factoryListAttribute.set(context.getList(clazz));
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
