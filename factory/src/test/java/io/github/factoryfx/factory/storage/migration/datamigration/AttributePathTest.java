package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.storage.migration.metadata.ExampleDataAPrevious;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttributePathTest {

    @Test
    public void test_resolve_string(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(exampleDataB);

        Assertions.assertEquals("1234", PathBuilder.value(String.class).pathElement("referenceAttribute").attribute("stringAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build()));
    }

    @Test
    public void test_resolve_string_root_attribute(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("1234");
        assertEquals("1234",PathBuilder.value(String.class).attribute("stringAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build()));
    }

    @Test
    public void test_resolve_ref(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(exampleDataB);


        ExampleDataB referenceAttribute = PathBuilder.value(ExampleDataB.class).attribute("referenceAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build());
        assertEquals("1234", referenceAttribute.stringAttribute.get());
    }

    @Test
    public void test_resolve_to_null(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(null);


        ExampleDataB referenceAttribute = PathBuilder.value(ExampleDataB.class).attribute("referenceAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build());
        assertNull(referenceAttribute);
    }



    @Test
    public void test_resolve_data_ref_id(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("1234");

        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataA.referenceAttribute.set(exampleDataB);
        exampleDataB.referenceAttribute.set(exampleDataA);


        DataJsonNode root = new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().writeValueAsTree(exampleDataA));


        ExampleDataA referenceAttribute = PathBuilder.value(ExampleDataA.class).pathElement("referenceAttribute").attribute("referenceAttribute").resolveAttributeValue(root,ObjectMapperBuilder.build());
        assertEquals("1234", referenceAttribute.stringAttribute.get());
    }


    @Test
    public void test_remove_path_check(){
        ExampleDataAPrevious root = new ExampleDataAPrevious();
        root.stringAttribute.set("1234");

        root.internal().finalise();
        DataStorageMetadataDictionary dictionary = root.internal().createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass("io.github.factoryfx.factory.storage.migration.metadata.ExampleDataAPrevious",ExampleDataA.class.getName());

        dictionary.markRemovedAttributes();

        assertTrue(PathBuilder.value(ExampleDataA.class).attribute("garbage").isPathToRemovedAttribute(dictionary, createDataJsonNode(root)));
    }

    @Test
    public void test_markRemovedAttributes_removedClass(){
        ExampleDataAPrevious root = new ExampleDataAPrevious();
        root.stringAttribute.set("1234");

        root.internal().finalise();
        DataStorageMetadataDictionary dictionary = root.internal().createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass("io.github.factoryfx.factory.storage.migration.metadata.ExampleDataAPrevious","a.b.c.Removed");

        dictionary.markRemovedAttributes();

        assertTrue(PathBuilder.value(ExampleDataA.class).attribute("garbage").isPathToRemovedAttribute(dictionary, createDataJsonNode(root)));
    }

    @Test
    public void test_resolve_reflist_string(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceListAttribute.add(new ExampleDataB());
        exampleDataA.referenceListAttribute.add(exampleDataB);

        assertEquals("1234",PathBuilder.value(String.class).pathElement("referenceListAttribute",1).attribute("stringAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build()));
    }

    public static class PathFactoryExample extends SimpleFactoryBase<Object,PathFactoryExample> {
        public final FactoryAttribute<PathFactoryExample, Void, PathFactoryExampleNested> reference = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class PathFactoryExampleNested extends SimpleFactoryBase<Void, PathFactoryExample> {
        public final FactoryAttribute<PathFactoryExample, Void, PathFactoryExampleNested> ref2 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_ref() {

        PathFactoryExample root = new PathFactoryExample();
        PathFactoryExampleNested factory = new PathFactoryExampleNested();
        factory.ref2.set(new PathFactoryExampleNested());
        root.reference.set(factory);
        root.internal().finalise();

        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = root.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.renameAttribute(PathFactoryExampleNested.class.getName(),"ref2","ref2Old");
        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(dataStorageMetadataDictionaryFromRoot));


        Assertions.assertFalse(new PathBuilder<>(ErrorPrinterFactory.class).pathElement("reference").attribute("ref2").isPathToRemovedAttribute(dataStorageMetadataDictionaryFromRoot,createDataJsonNode(root)));
        Assertions.assertTrue(new PathBuilder<>(ErrorPrinterFactory.class).pathElement("reference").attribute("ref2Old").isPathToRemovedAttribute(dataStorageMetadataDictionaryFromRoot,createDataJsonNode(root)));
    }

    public static class PolymorphicFactoryExample extends SimpleFactoryBase<Object,PolymorphicFactoryExample> {
        public final FactoryPolymorphicAttribute<ExampleFactoryA, Printer> polyreference = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    private static class AttributePrinterFactory extends PolymorphicFactoryBase<Printer,ExampleFactoryA> {
        public final StringAttribute attribute = new StringAttribute();
        @Override
        protected Printer createImpl() {
            return new ErrorPrinter();
        }

    }

    @Test
    public void test_polymorphic_ref() {

        PolymorphicFactoryExample root = new PolymorphicFactoryExample();
        root.polyreference.set(new AttributePrinterFactory());
        root.internal().finalise();

        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = root.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.renameAttribute(AttributePrinterFactory.class.getName(),"attribute","attributeOld");
        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();

        Assertions.assertTrue(new PathBuilder<>(ErrorPrinterFactory.class).pathElement("polyreference").attribute("attributeOld").isPathToRemovedAttribute(dataStorageMetadataDictionaryFromRoot,createDataJsonNode(root)));
        Assertions.assertFalse(new PathBuilder<>(ErrorPrinterFactory.class).pathElement("polyreference").attribute("attribute").isPathToRemovedAttribute(dataStorageMetadataDictionaryFromRoot,createDataJsonNode(root)));
    }

    private DataJsonNode createDataJsonNode(FactoryBase<?,?> factory){
        return new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().writeValueAsTree(factory));

    }


    @Test
    public void test_resolve_refList(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceListAttribute.add(exampleDataB);


        ExampleDataB referenceAttribute = PathBuilder.value(ExampleDataB.class).attribute("referenceListAttribute").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build());
        assertEquals("1234", referenceAttribute.stringAttribute.get());
    }

    @Test
    public void test_resolve_refList_index(){
        ExampleDataA exampleDataA = new ExampleDataA();
        {
            ExampleDataB exampleDataB = new ExampleDataB();
            exampleDataB.stringAttribute.set("1111");
            exampleDataA.referenceListAttribute.add(exampleDataB);
        }
        {
            ExampleDataB exampleDataB = new ExampleDataB();
            exampleDataB.stringAttribute.set("2222");
            exampleDataA.referenceListAttribute.add(exampleDataB);
        }

        ExampleDataB exampleDataResolved = PathBuilder.of(ExampleDataB.class,"referenceListAttribute[1]").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build());
        assertEquals("2222", exampleDataResolved.stringAttribute.get());
    }

    @Test
    public void test_resolve_refList_index_idref(){
        ExampleDataA exampleDataA = new ExampleDataA();
        {
            ExampleDataB exampleDataB = new ExampleDataB();
            exampleDataB.stringAttribute.set("1111");
            exampleDataA.referenceListAttribute.add(exampleDataB);
            exampleDataA.referenceListAttribute.add(exampleDataB);
        }

        ExampleDataB exampleDataResolved = PathBuilder.of(ExampleDataB.class,"referenceListAttribute[1]").resolveAttributeValue(createDataJsonNode(exampleDataA),ObjectMapperBuilder.build());
        assertEquals("1111", exampleDataResolved.stringAttribute.get());
    }

}