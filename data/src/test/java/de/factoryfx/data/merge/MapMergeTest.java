package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringMapAttribute;
import org.junit.Assert;
import org.junit.Test;

public class MapMergeTest extends MergeHelperTestBase{

    public static class MapExampleFactory extends Data {
        public final StringMapAttribute mapAttribute =new StringMapAttribute();

    }


    @Test
    public void test_same(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().prepareUsableCopy();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v3");
        update=update.internal().prepareUsableCopy();

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(3, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assert.assertEquals("v3", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_different(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().prepareUsableCopy();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v4");
        update=update.internal().prepareUsableCopy();

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(3, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assert.assertEquals("v4", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_deleted(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");
        original.mapAttribute.get().put("k3","v3");
        original=original.internal().prepareUsableCopy();

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update=update.internal().prepareUsableCopy();

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(2, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));

    }

}