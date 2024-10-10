package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R> root
 */
public class FactoryTreeBuilderBasedAttributeSetup<R extends FactoryBase<?,R>> {

    private final FactoryTreeBuilder<?,R> factoryTreeBuilder;

    public FactoryTreeBuilderBasedAttributeSetup(FactoryTreeBuilder<?,R> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    @SuppressWarnings("unchecked")
    public <LO, FO extends FactoryBase<LO, R>> List<FO> createNewFactory(Class<LO> liveObjectClass) {
        List<FO> newFactories =  factoryTreeBuilder.buildSubTreesForLiveObject(liveObjectClass);
        ArrayList<FO> result = new ArrayList<>(newFactories);
        if(result.isEmpty()){
//            FactoryMetadata<R, FO> factoryMetadata = FactoryMetadataManager.getMetadataUnsafe(clazz);
//            FO instance = factoryMetadata.newInstance();
//            result.add(instance);
        }
        return result;
    }

    private void setupReferenceAttribute(AttributeMetadata attributeMetadata,FactoryAttribute<?, ?> referenceAttribute) {
        Class<? extends FactoryBase<?,?>> referenceClass = attributeMetadata.referenceClass;
        Scope scope = factoryTreeBuilder.getScope(new FactoryTemplateId<>(referenceClass,null));
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
    }

    private void setupReferenceListAttribute(AttributeMetadata attributeMetadata,FactoryListAttribute<?, ?> referenceAttribute) {
        Class<? extends FactoryBase<?,?>> referenceClass = attributeMetadata.referenceClass;
        Scope scope = factoryTreeBuilder.getScope(new FactoryTemplateId<>(referenceClass,null));
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
    }

    private void applyToAttribute(AttributeMetadata attributeMetadata, Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryAttribute){
            setupReferenceAttribute(attributeMetadata,(FactoryAttribute)attribute);
        }
        if (attribute instanceof FactoryListAttribute){
            setupReferenceListAttribute(attributeMetadata,(FactoryListAttribute)attribute);
        }
    }

    public void applyToRootFactoryDeep(R root) {
        root.internal().setFactoryTreeBuilderBasedAttributeSetupForRoot(this);
        root.internal().setFactoryTreeBuilder(this.factoryTreeBuilder);
        factoryTreeBuilder.fillFromExistingFactoryTree(root);

        root.internal().collectChildrenDeep().forEach(data -> {
            data.internal().visitAttributesFlat(this::applyToAttribute);
        });
    }
}
