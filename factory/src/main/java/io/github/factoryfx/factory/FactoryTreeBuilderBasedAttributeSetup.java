package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.ArrayList;
import java.util.List;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R> root
 */
public class FactoryTreeBuilderBasedAttributeSetup<R extends FactoryBase<?,R>> {

    private final FactoryTreeBuilder<?,R> factoryTreeBuilder;

    public FactoryTreeBuilderBasedAttributeSetup(FactoryTreeBuilder<?,R> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public <LO, FO extends FactoryBase<LO, R>> List<FO> createNewFactory(Class<FO> clazz) {
        List<FO> newFactories =  factoryTreeBuilder.buildSubTrees(clazz);
        ArrayList<FO> result = new ArrayList<>(newFactories);
        if(result.isEmpty()){
            FactoryMetadata<R, FO> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);
            FO instance = factoryMetadata.newInstance();
            factoryMetadata.setAttributeReferenceClasses(instance);
            result.add(instance);
        }
        return result;
    }

    private void setupReferenceAttribute(FactoryAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
    }

    private void setupReferenceListAttribute(FactoryListAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
    }

    private void applyToAttribute(Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryAttribute){
            setupReferenceAttribute((FactoryAttribute)attribute);
        }
        if (attribute instanceof FactoryListAttribute){
            setupReferenceListAttribute((FactoryListAttribute)attribute);
        }
    }

    public void applyToRootFactoryDeep(R root) {
        root.internal().serFactoryTreeBuilderBasedAttributeSetupForRoot(this);
        factoryTreeBuilder.fillFromExistingFactoryTree(root);

        root.internal().collectChildrenDeep().forEach(data -> {
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> applyToAttribute(attribute));
        });
    }
}
