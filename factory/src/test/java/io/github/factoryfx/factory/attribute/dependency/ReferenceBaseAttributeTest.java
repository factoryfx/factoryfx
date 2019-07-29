package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReferenceBaseAttributeTest {

    public static class GenericData<T> extends FactoryBase<Void, GenericData<T>> {

    }

    @Test
    public void test_generic_declaration(){
        FactoryAttribute<Void,GenericData<String>> test1 = new FactoryAttribute<>();
    }

    @Test
    public void test_AttributeStorageMetadata(){
        FactoryAttribute<Void,GenericData<String>> test1 = new FactoryAttribute<>();
        AttributeStorageMetadata attributeStorageMetadata = test1.createAttributeStorageMetadata("bla");
        AttributeStorageMetadata attributeStorageMetadata2 = test1.createAttributeStorageMetadata("bla");
        Assertions.assertTrue(attributeStorageMetadata.isReference());
        Assertions.assertTrue(attributeStorageMetadata.match(attributeStorageMetadata2));
    }

    @Test
    public void test_copy_root_after_jackson(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("dfssfdsfdsfd");

        exampleFactoryA.referenceAttribute.set(null);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());
        exampleFactoryA = exampleFactoryA.internal().finalise();


        ExampleDataA copy  = ObjectMapperBuilder.build().copy(exampleFactoryA);
        //also include jackson cause into copy test,(strange jackson behaviour for final fields)
        //ObjectMapperBuilder internally call internal().copy

        Assertions.assertEquals(copy, copy.internal().getRoot());
        copy.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof ReferenceBaseAttribute){
                Assertions.assertEquals(copy,((ReferenceBaseAttribute)attribute).root);
            }
        });

    }


}