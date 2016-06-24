package de.factoryfx.factory.attribute.builder;

public class AttributeBuilder {



//    public <T extends FactoryBase<?,T>> AttributeMetadataBuilder<ObservableList<T>,ReferenceListAttribute<T>> referenceList(Class<T> clazz){
//        ReferenceListAttribute<T> referenceListAttribute = new ReferenceListAttribute<>(attributeMetadata);
//        referenceListAttribute.possibleValueProviderFromRoot= Optional.of(factoryBase -> {
//            List<FactoryBase<?,?>> result = new ArrayList<>();
//            for (FactoryBase<?, ?>  factory: factoryBase.collectChildFactories()){
//                if (clazz.isAssignableFrom(factory.getClass())){
//                    result.add(factory);
//                }
//            }
//            return result;
//        });
//        return new AttributeMetadataBuilder<>(referenceListAttribute);
//    }
//
//    public <T extends FactoryBase<?,T>> AttributeMetadataBuilder<ObservableList<T>,ReferenceListAttribute<T>> referenceList(Function<FactoryBase<?,?>,List<FactoryBase<?,?>>> supplier){
//        ReferenceListAttribute<T> referenceListAttribute = new ReferenceListAttribute<>(attributeMetadata);
//        referenceListAttribute.possibleValueProviderFromRoot= Optional.of(supplier);
//        return new AttributeMetadataBuilder<>(referenceListAttribute);
//    }
//
//    public <T extends FactoryBase<?,T>> AttributeMetadataBuilder<T,ReferenceAttribute<T>> reference(){
//        return new AttributeMetadataBuilder<>(new ReferenceAttribute<T>(attributeMetadata));
//    }
}
