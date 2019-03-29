package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringMapAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MapMergeTest extends MergeHelperTestBase{

    public static class MapExampleFactory extends FactoryBase<Void, MapExampleFactory> {
        public final StringMapAttribute mapAttribute =new StringMapAttribute();

    }


    @Test
    public void test_same(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().addBackReferences();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v3");
        update=update.internal().addBackReferences();

        Assertions.assertTrue(merge(original, original, update).hasNoConflicts());
        Assertions.assertEquals(3, original.mapAttribute.get().size());
        Assertions.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assertions.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assertions.assertEquals("v3", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_different(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().addBackReferences();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v4");
        update=update.internal().addBackReferences();

        Assertions.assertTrue(merge(original, original, update).hasNoConflicts());
        Assertions.assertEquals(3, original.mapAttribute.get().size());
        Assertions.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assertions.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assertions.assertEquals("v4", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_deleted(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().addBackReferences();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update=update.internal().addBackReferences();

        Assertions.assertTrue(merge(original, original, update).hasNoConflicts());
        Assertions.assertEquals(2, original.mapAttribute.get().size());
        Assertions.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assertions.assertEquals("v2", original.mapAttribute.get().get("k2"));

    }

}