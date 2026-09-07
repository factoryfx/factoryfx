package io.github.factoryfx.factory.storage.migration.metadata;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DataStorageMetadataDictionaryTest {

    @Test
    public void test_json(){
        ObjectMapperBuilder.build().copy(createDataStorageMetadataDictionaryFromRoot());
//        System.out.println( ObjectMapperBuilder.build().writeValueAsString(new DataStorageMetadataDictionary(ExampleDataA.class)));
    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionaryFromRoot() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();
        return exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
    }

    @Test
    public void test_json_stable(){
        Assertions.assertEquals(ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()),ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()));
    }

    @Test
    public void test_init(){
        Assertions.assertEquals(1, createDataStorageMetadataDictionaryFromRoot().dataList.size());
    }

    @Test
    public void test_rename_root(){
        DataStorageMetadataDictionary dictionary = createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass(ExampleDataA.class.getName(),"a.b.C");
        Assertions.assertEquals("a.b.C",dictionary.rootClass);
    }

    @Test
    public void test_retype_attribute_class(){
        DataStorageMetadataDictionary dictionary = createDataStorageMetadataDictionaryFromRoot();
        //simulate a stored dictionary written before the attribute class was renamed to its current name
        dictionary.retypeAttributeClass(StringAttribute.class.getName(),"a.b.OldStringAttribute");
        dictionary.markRetypedAttributes();
        Assertions.assertTrue(dictionary.isRetypedAttribute(ExampleDataA.class.getName(),"stringAttribute"));

        DataStorageMetadataDictionary staleDictionary = createDataStorageMetadataDictionaryFromRoot();
        staleDictionary.retypeAttributeClass(StringAttribute.class.getName(),"a.b.OldStringAttribute");
        staleDictionary.retypeAttributeClass("a.b.OldStringAttribute",StringAttribute.class.getName());
        staleDictionary.markRetypedAttributes();
        Assertions.assertFalse(staleDictionary.isRetypedAttribute(ExampleDataA.class.getName(),"stringAttribute"));
        Assertions.assertEquals(StringAttribute.class.getName(),staleDictionary.getAttributeStorageMetadata(ExampleDataA.class.getName(),"stringAttribute").getAttributeClassName());
    }

    @Test
    public void test_rename_attributeref(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());
        exampleDataA.internal().finalise();
        DataStorageMetadataDictionary dictionary  = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass(ExampleDataB.class.getName(),"a.b.C");
        Assertions.assertEquals("a.b.C",dictionary.getDataStorageMetadata("a.b.C").getClassName());
        Assertions.assertEquals("a.b.C",dictionary.getDataStorageMetadata(ExampleDataA.class.getName()).getAttribute("referenceAttribute").referenceClass);
    }
}