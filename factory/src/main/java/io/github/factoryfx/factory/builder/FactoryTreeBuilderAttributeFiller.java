package io.github.factoryfx.factory.builder;


import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.storage.migration.datamigration.AttributeFiller;
import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
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
        for (Data data : root.internal().collectChildrenDeep()) {

//            Class aClass = ((FactoryReferenceAttribute<?, ?>) attribute).internal_getReferenceClass();


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

                data.internal().visitAttributesDualFlat(newBuild, (attributeVariableName, newAttribute, buildedAttribute) -> {
                    if (!oldDataStorageMetadataDictionary.containsAttribute(data.getClass().getName(), attributeVariableName)) {//is new Attribute

//                        if (attribute instanceof FactoryReferenceAttribute<?,?>){
//                            Class aClass = ((FactoryReferenceAttribute<?, ?>) attribute).internal_getReferenceClass();
//                            FactoryBase<?,?,R> factoryBase = factoryTreeBuilder.buildSubTree(aClass);
//                            attribute.set(factoryBase);
//                        }
                        Object o = buildedAttribute.get();
                        ((Attribute) newAttribute).set(o);
                    }
                });
            }


        }
    }
}

