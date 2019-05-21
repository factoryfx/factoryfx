package io.github.factoryfx.factory.builder;



import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeFiller;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.FactoryBase;

public class FactoryTreeBuilderAttributeFiller<L,R extends FactoryBase<L,R>,S> implements AttributeFiller<R> {

    private final FactoryTreeBuilder<L, R, S> factoryTreeBuilder;

    public FactoryTreeBuilderAttributeFiller(FactoryTreeBuilder<L, R, S> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary) {
        factoryTreeBuilder.fillFromExistingFactoryTree(root);
        for (FactoryBase<?,R> factory : root.internal().collectChildrenDeep()) {
            boolean[] containsNewAttributes=new boolean[1];
            factory.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (isNewAttribute(factory, oldDataStorageMetadataDictionary, attributeVariableName)) {
                    containsNewAttributes[0]=true;
                    oldDataStorageMetadataDictionary.containsAttribute(factory.getClass().getName(), attributeVariableName);
                }
            });
            if (containsNewAttributes[0]){
                Class aClass = factory.getClass();
                FactoryBase<?, R> newBuild = factoryTreeBuilder.buildNewSubTree(aClass);
                FactoryMetadataManager.getMetadata(newBuild.getClass()).addBackReferencesAndReferenceClassToAttributes(newBuild,root);

                this.fillNewAttributes(factory,newBuild,oldDataStorageMetadataDictionary);
            }
        }
    }

    private <FO extends FactoryBase<?,R>> void fillNewAttributes(FO factory, FO newBuild, DataStorageMetadataDictionary oldDataStorageMetadataDictionary){
        factory.internal().visitAttributesForMatch(newBuild, (attributeVariableName, newAttribute, newlyBuildAttribute) -> {
            if (isNewAttribute(factory, oldDataStorageMetadataDictionary, attributeVariableName)) {
                newAttribute.set(newlyBuildAttribute.get());
            }
            return true;
        });
    }

    private <FO extends FactoryBase<?, R>> boolean isNewAttribute(FO currentFactoryRoot, DataStorageMetadataDictionary oldDataStorageMetadataDictionary, String attributeVariableName) {
        return !oldDataStorageMetadataDictionary.containsAttribute(currentFactoryRoot.getClass().getName(), attributeVariableName);
    }


}

