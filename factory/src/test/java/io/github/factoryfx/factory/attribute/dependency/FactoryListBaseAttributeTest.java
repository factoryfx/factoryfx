package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactoryListBaseAttributeTest {

    public static class ExampleFactoryACatalog extends ExampleFactoryA{
        public final FactoryListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceList2Attribute = new FactoryListAttribute<ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA3");
    }


    public static class ExampleFactoryBCatalog extends ExampleFactoryB{
        public ExampleFactoryBCatalog(){
            this.config().markAsCatalogItem();
        }
    }

    @Test
    public void test_delete_catalog_ref(){
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.internal().finalise();

        Assertions.assertNotNull(root.referenceAttribute.get());
        root.referenceListAttribute.internal_deleteFactory(catalog);
        Assertions.assertNull(root.referenceAttribute.get());
    }

    @Test
    public void test_delete_catalog_list(){
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.referenceList2Attribute.add(catalog);
        root.internal().finalise();

        Assertions.assertTrue(root.referenceList2Attribute.contains(catalog));
        root.referenceListAttribute.internal_deleteFactory(catalog);
        Assertions.assertFalse(root.referenceList2Attribute.contains(catalog));
    }
}