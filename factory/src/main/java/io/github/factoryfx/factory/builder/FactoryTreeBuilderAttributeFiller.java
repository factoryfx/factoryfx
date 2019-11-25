package io.github.factoryfx.factory.builder;



import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeFiller;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.FactoryBase;

public class FactoryTreeBuilderAttributeFiller<L,R extends FactoryBase<L,R>> implements AttributeFiller<R> {

    private final FactoryTreeBuilder<L, R> factoryTreeBuilder;

    public FactoryTreeBuilderAttributeFiller(FactoryTreeBuilder<L, R> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary) {
        factoryTreeBuilder.fillFromExistingFactoryTree(root);
        for (FactoryBase<?,R> factory : root.internal().collectChildrenDeep()) {
            boolean[] containsNewAttributes=new boolean[1];
            factory.internal().visitAttributesMetadata((attributeMetadata) -> {
                if (isNewAttribute(factory, oldDataStorageMetadataDictionary, attributeMetadata.attributeVariableName)) {
                    containsNewAttributes[0]=true;
                    oldDataStorageMetadataDictionary.containsAttribute(factory.getClass().getName(), attributeMetadata.attributeVariableName);
                }
            });
            if (containsNewAttributes[0]){
                Class aClass = factory.getClass();
                FactoryBase<?, R> newBuild = factoryTreeBuilder.buildNewSubTree(aClass);
                FactoryMetadataManager.getMetadata(newBuild.getClass()).addBackReferencesToAttributes(newBuild,root);

                this.fillNewAttributes(factory,newBuild,oldDataStorageMetadataDictionary);
            }
        }
    }

    private <FO extends FactoryBase<?,R>> void fillNewAttributes(FO factory, FO newBuild, DataStorageMetadataDictionary oldDataStorageMetadataDictionary){
        factory.internal().visitAttributesForMatch(newBuild, (attributeVariableName, newAttribute, newlyBuildAttribute) -> {
            if (isNewAttribute(factory, oldDataStorageMetadataDictionary, attributeVariableName)) {
                if(newAttribute.get() == null) {
                    newAttribute.set(newlyBuildAttribute.get());
                }
            }
            return true;
        });
    }

    private <FO extends FactoryBase<?, R>> boolean isNewAttribute(FO currentFactoryRoot, DataStorageMetadataDictionary oldDataStorageMetadataDictionary, String attributeVariableName) {
        return !oldDataStorageMetadataDictionary.containsAttribute(currentFactoryRoot.getClass().getName(), attributeVariableName);
    }


}

