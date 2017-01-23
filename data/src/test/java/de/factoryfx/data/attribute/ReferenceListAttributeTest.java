package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceListAttributeTest {

    public static class ExampleReferenceListFactory extends IdData {
        public ReferenceListAttribute<ExampleFactoryA> referenceListAttribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());


    }

    @Test
    public void testObservable(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceListFactory.referenceListAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceListFactory.referenceListAttribute.get().add(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceListFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<List<ExampleFactoryA>> invalidationListener = (a,o) -> {
            calls.add("");
        };
        exampleReferenceListFactory.referenceListAttribute.internal_addListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceListFactory.referenceListAttribute.internal_removeListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        exampleReferenceListFactory.referenceListAttribute.internal_prepareUsage(new ExampleReferenceListFactory());
        exampleReferenceListFactory.referenceListAttribute.addNewFactory();
        Assert.assertEquals(1,exampleReferenceListFactory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        exampleReferenceListFactory = exampleReferenceListFactory.internal().prepareUsableCopy();


        Collection<ExampleFactoryA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.possibleValues();
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA.getId(),new ArrayList<>(possibleFactories).get(0).getId());

    }

    @Test
    public void test_add_new_listener(){
        ReferenceListAttribute<ExampleFactoryA> referenceListAttribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        referenceListAttribute.internal_prepareUsage(new ExampleFactoryA());
        List<ExampleFactoryA> calls=new ArrayList<>();
        referenceListAttribute.internal_addListener((attribute, value) -> calls.add(value.get(0)));
        referenceListAttribute.addNewFactory();
        Assert.assertEquals(1,calls.size());
        referenceListAttribute.addNewFactory();
        Assert.assertEquals(2,calls.size());
//        Assert.assertEquals(value,calls.get(0));
    }

    @Test
    public void removeListener() throws Exception {
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        final AttributeChangeListener<List<ExampleFactoryA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assert.assertEquals(1,attribute.listeners.size());
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        final AttributeChangeListener<List<ExampleFactoryA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        final AttributeChangeListener<List<ExampleFactoryA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));//null to simulate garbage collected weakref
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void delegate_root_for_added() {
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        attribute.internal_prepareUsage(new ExampleFactoryB());

        final ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        Assert.assertFalse(exampleFactoryA.internal().readyForUsage());
        attribute.get().add(exampleFactoryA);
        Assert.assertTrue(exampleFactoryA.internal().readyForUsage());
    }

    @Test
    public void delegate_root_for_addAll(){
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        attribute.internal_prepareUsage(new ExampleFactoryB());

        final ExampleFactoryA exampleFactoryA1 = new ExampleFactoryA();
        final ExampleFactoryA exampleFactoryA2 = new ExampleFactoryA();
        Assert.assertFalse(exampleFactoryA1.internal().readyForUsage());
        Assert.assertFalse(exampleFactoryA2.internal().readyForUsage());
        attribute.get().addAll(Arrays.asList(exampleFactoryA1,exampleFactoryA2));
        Assert.assertTrue(exampleFactoryA1.internal().readyForUsage());
        Assert.assertTrue(exampleFactoryA2.internal().readyForUsage());
    }

    @Test
    public void delegate_root_for_addAll_with_null(){
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        attribute.internal_prepareUsage(new ExampleFactoryB());
        attribute.get().add(null);
    }

    @Test(expected = IllegalStateException.class)
    public void delegate_root_for_addAll_with_null_nested(){
        ReferenceListAttribute<ExampleFactoryA> attribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        attribute.internal_prepareUsage(new ExampleFactoryB());

        final ExampleFactoryA exampleFactoryA1 = new ExampleFactoryA();
        exampleFactoryA1.referenceListAttribute.add(null);
        attribute.get().add(exampleFactoryA1);
        Assert.assertTrue(exampleFactoryA1.internal().readyForUsage());
    }
}