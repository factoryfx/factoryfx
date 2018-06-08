package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceListAttributeTest {

    public static class ExampleReferenceListFactory extends Data {
        public DataReferenceListAttribute<ExampleDataA> referenceListAttribute =new DataReferenceListAttribute<>(ExampleDataA.class);


    }

    @Test
    public void testObservable(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceListFactory.referenceListAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleDataA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json() {
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        {
            ExampleDataA exampleFactoryA = new ExampleDataA();
            exampleFactoryA.stringAttribute.set("sadsasd");
            exampleReferenceListFactory.referenceListAttribute.get().add(exampleFactoryA);
        }
        {
            ExampleDataA exampleFactoryA = new ExampleDataA();
            exampleFactoryA.stringAttribute.set("sadsasd");
            exampleReferenceListFactory.referenceListAttribute.get().add(exampleFactoryA);
        }
        ObjectMapperBuilder.build().copy(exampleReferenceListFactory);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleReferenceListFactory));
    }

    @Test
    public void remove_Listener(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> invalidationListener = (a, o) -> {
            calls.add("");
        };
        exampleReferenceListFactory.referenceListAttribute.internal_addListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleDataA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceListFactory.referenceListAttribute.internal_removeListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleDataA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        exampleReferenceListFactory.referenceListAttribute.internal_prepareUsageFlat(new ExampleReferenceListFactory(),null);
        List<ExampleDataA> exampleDataAS = exampleReferenceListFactory.referenceListAttribute.internal_createNewPossibleValues();
        exampleReferenceListFactory.referenceListAttribute.add(exampleDataAS.get(0));
        Assert.assertEquals(1,exampleReferenceListFactory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        String expectedId=exampleFactoryA.getId();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        exampleReferenceListFactory = exampleReferenceListFactory.internal().addBackReferences();


        Collection<ExampleDataA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.internal_possibleValues();
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(expectedId,new ArrayList<>(possibleFactories).get(0).getId());

    }

    @Test
    public void test_add_new_listener(){
        DataReferenceListAttribute<ExampleDataA> referenceListAttribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        referenceListAttribute.internal_prepareUsageFlat(new ExampleDataA(),null);
        List<ExampleDataA> calls=new ArrayList<>();
        referenceListAttribute.internal_addListener((attribute, value) -> calls.add(value.get(0)));

        referenceListAttribute.add(referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assert.assertEquals(1,calls.size());

        referenceListAttribute.add(referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assert.assertEquals(2,calls.size());
//        Assert.assertEquals(value,calls.get(0));
    }

    @Test
    public void removeListener() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assert.assertEquals(1,attribute.listeners.size());
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));//null to simulate garbage collected weakref
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void delegate_root_for_added() {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        attribute.internal_prepareUsageFlat(new ExampleDataB(),null);

        final ExampleDataA exampleFactoryA = new ExampleDataA();
        Assert.assertFalse(exampleFactoryA.internal().readyForUsage());
        attribute.get().add(exampleFactoryA);
        Assert.assertTrue(exampleFactoryA.internal().readyForUsage());
    }

    @Test
    public void delegate_root_for_addAll(){
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        attribute.internal_prepareUsageFlat(new ExampleDataB(),null);

        final ExampleDataA exampleFactoryA1 = new ExampleDataA();
        final ExampleDataA exampleFactoryA2 = new ExampleDataA();
        Assert.assertFalse(exampleFactoryA1.internal().readyForUsage());
        Assert.assertFalse(exampleFactoryA2.internal().readyForUsage());
        attribute.get().addAll(Arrays.asList(exampleFactoryA1,exampleFactoryA2));
        Assert.assertTrue(exampleFactoryA1.internal().readyForUsage());
        Assert.assertTrue(exampleFactoryA2.internal().readyForUsage());
    }

    @Test(expected = IllegalStateException.class)
    public void delegate_root_for_addAll_with_null(){
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        attribute.internal_prepareUsageFlat(new ExampleDataB(),null);
        attribute.get().add(null);
    }

    @Test(expected = IllegalStateException.class)
    public void delegate_root_for_addAll_with_null_nested(){
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        attribute.internal_prepareUsageFlat(new ExampleDataB(),null);

        final ExampleDataA exampleFactoryA1 = new ExampleDataA();
        exampleFactoryA1.referenceListAttribute.add(null);
        attribute.get().add(exampleFactoryA1);
        Assert.assertTrue(exampleFactoryA1.internal().readyForUsage());
    }

    @Test
    public void test_semanticcopy_self(){
        DataReferenceListAttribute<ExampleDataA> attributeFrom =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.add(new ExampleDataA());
        DataReferenceListAttribute<ExampleDataA> attributeTo =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assert.assertTrue("same reference",attributeFrom.get(0)==attributeTo.get(0));
    }

    @Test
    public void test_semanticcopy_copy(){
        DataReferenceListAttribute<ExampleDataA> attributeFrom =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.add(new ExampleDataA());
        DataReferenceListAttribute<ExampleDataA> attributeTo =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assert.assertTrue("not same reference",attributeFrom.get(0)!=attributeTo.get(0));
        Assert.assertNotEquals(attributeFrom.get(0).getId(),attributeTo.get(0).getId());
    }
}