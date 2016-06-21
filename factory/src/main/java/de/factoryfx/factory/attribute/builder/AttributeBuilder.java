package de.factoryfx.factory.attribute.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import javafx.collections.ObservableList;

public class AttributeBuilder {


    public static AttributeMetadataBuilder<String,StringAttribute> string(){
        return new AttributeMetadataBuilder<>(new StringAttribute());
    }

    public static AttributeMetadataBuilder<String,StringAttribute> string(String defaultValue){
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set(defaultValue);
        return new AttributeMetadataBuilder<>(stringAttribute);
    }

    public static AttributeMetadataBuilder<Integer,IntegerAttribute> integer(){
        return new AttributeMetadataBuilder<>(new IntegerAttribute());
    }

    public static <T extends FactoryBase<?,? super T>> AttributeMetadataBuilder<ObservableList<T>,ReferenceListAttribute<T>> referenceList(){
        ReferenceListAttribute<T> referenceListAttribute = new ReferenceListAttribute<>();
        return new AttributeMetadataBuilder<>(referenceListAttribute);
    }

    public static <T extends FactoryBase<?,? super T>> AttributeMetadataBuilder<ObservableList<T>,ReferenceListAttribute<T>> referenceList(Class<T> clazz){
        ReferenceListAttribute<T> referenceListAttribute = new ReferenceListAttribute<>();
        referenceListAttribute.possibleValueProviderFromRoot= Optional.of(factoryBase -> {
            List<FactoryBase<?,?>> result = new ArrayList<>();
            for (FactoryBase<?, ?>  factory: factoryBase.collectChildFactories()){
                if (clazz.isAssignableFrom(factory.getClass())){
                    result.add(factory);
                }
            }
            return result;
        });
        return new AttributeMetadataBuilder<>(referenceListAttribute);
    }

    public static <T extends FactoryBase<?,? super T>> AttributeMetadataBuilder<ObservableList<T>,ReferenceListAttribute<T>> referenceList(Function<FactoryBase<?,?>,List<FactoryBase<?,?>>> supplier){
        ReferenceListAttribute<T> referenceListAttribute = new ReferenceListAttribute<>();
        referenceListAttribute.possibleValueProviderFromRoot= Optional.of(supplier);
        return new AttributeMetadataBuilder<>(referenceListAttribute);
    }

    public static <T extends FactoryBase<?,? super T>> AttributeMetadataBuilder<T,ReferenceAttribute<T>> reference(){
        return new AttributeMetadataBuilder<>(new ReferenceAttribute<T>());
    }
}
