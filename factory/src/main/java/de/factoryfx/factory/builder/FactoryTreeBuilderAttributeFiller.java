package de.factoryfx.factory.builder;


import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.storage.migration.datamigration.AttributeFiller;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.FactoryBase;

public class FactoryTreeBuilderAttributeFiller<V,L,R extends FactoryBase<L,V,R>,S> implements AttributeFiller<R> {

    private final FactoryTreeBuilder<V, L, R, S> factoryTreeBuilder;

    public FactoryTreeBuilderAttributeFiller(FactoryTreeBuilder<V, L, R, S> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    @Override
    public void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary) {
        factoryTreeBuilder.fillFromExistingFactoryTree(root);
        for (Data data : root.internal().collectChildrenDeep()) {

//            Class aClass = ((FactoryReferenceAttribute<?, ?>) attribute).internal_getReferenceClass();


            boolean[] containsNewAttributes=new boolean[1];
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (!oldDataStorageMetadataDictionary.containsAttribute(data.getClass().getName(), attributeVariableName)) {//is new Attribute
                    containsNewAttributes[0]=true;
                }
            });
            if (containsNewAttributes[0]){
                Class aClass = data.getClass();
                FactoryBase<?, ?, R> newBuild = factoryTreeBuilder.buildSubTree(aClass);

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

