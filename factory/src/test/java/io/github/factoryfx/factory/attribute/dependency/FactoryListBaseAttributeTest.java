package io.github.factoryfx.factory.attribute.dependency;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;

class FactoryListBaseAttributeTest {

    public static class ExampleFactoryACatalog extends ExampleFactoryA {
        public final FactoryListAttribute<ExampleLiveObjectB, ExampleFactoryB> referenceList2Attribute = new FactoryListAttribute<ExampleLiveObjectB, ExampleFactoryB>().labelText("ExampleA3");
    }

    public static class ExampleFactoryBCatalog extends ExampleFactoryB {
        public ExampleFactoryBCatalog() {
            this.config().markAsCatalogItem();
        }
    }

    @Test
    public void test_delete_catalog_ref() {
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.internal().finalise();

        assertNotNull(root.referenceAttribute.get());
        root.referenceListAttribute.internal_deleteFactory(catalog);
        Assertions.assertEquals(catalog, root.referenceAttribute.get());
    }

    @Test
    public void test_remove_catalog_ref() {
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        root.referenceListAttribute.destroyOnRemove();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.internal().finalise();

        assertNotNull(root.referenceAttribute.get());
        root.referenceListAttribute.internal_deleteFactory(catalog);
        assertNull(root.referenceAttribute.get());
    }

    @Test
    public void test_delete_catalog_list() {
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        root.referenceListAttribute.destroyOnRemove();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.referenceList2Attribute.add(catalog);
        root.internal().finalise();

        assertTrue(root.referenceList2Attribute.contains(catalog));
        root.referenceListAttribute.internal_deleteFactory(catalog);
        assertFalse(root.referenceList2Attribute.contains(catalog));
    }

    @Test
    public void test_remove_non_existant() {
        ExampleFactoryA root = new ExampleFactoryA();
        root.internal().finalise();
        root.referenceListAttribute.remove(new ExampleFactoryB());

        assertTrue(root.getModified().isEmpty());
    }

    @Test
    public void test_remove_catalog_list() {
        ExampleFactoryACatalog root = new ExampleFactoryACatalog();
        ExampleFactoryBCatalog catalog = new ExampleFactoryBCatalog();
        root.referenceAttribute.set(catalog);
        root.referenceListAttribute.add(catalog);
        root.referenceList2Attribute.add(catalog);
        root.internal().finalise();

        assertTrue(root.referenceList2Attribute.contains(catalog));
        root.referenceListAttribute.internal_deleteFactory(catalog);
        assertFalse(root.referenceListAttribute.contains(catalog));
        assertTrue(root.referenceList2Attribute.contains(catalog));
    }
}