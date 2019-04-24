package io.github.factoryfx.factory.builder;



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
        for (FactoryBase<?,R> data : root.internal().collectChildrenDeep()) {
            boolean[] containsNewAttributes=new boolean[1];
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (!oldDataStorageMetadataDictionary.containsAttribute(data.getClass().getName(), attributeVariableName)) {//is new Attribute
                    containsNewAttributes[0]=true;
                    oldDataStorageMetadataDictionary.containsAttribute(data.getClass().getName(), attributeVariableName);
                }
            });
            if (containsNewAttributes[0]){
                Class aClass = data.getClass();
                FactoryBase<?, R> newBuild = factoryTreeBuilder.buildNewSubTree(aClass);

                this.fillNewAttributes(data,newBuild,oldDataStorageMetadataDictionary);
            }
        }
    }

    private <FO extends FactoryBase<?,R>> void fillNewAttributes(FO currentFactoryRoot, FO newBuild, DataStorageMetadataDictionary oldDataStorageMetadataDictionary){
        currentFactoryRoot.internal().visitAttributesForMatch(newBuild, (attributeVariableName, newAttribute, buildedAttribute) -> {
            if (!oldDataStorageMetadataDictionary.containsAttribute(currentFactoryRoot.getClass().getName(), attributeVariableName)) {//is new Attribute
                Object o = buildedAttribute.get();
                newAttribute.set(buildedAttribute.get());
            }
            return true;
        });
    }
}

