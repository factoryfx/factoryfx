package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends Data {
        public DataReferenceAttribute<ExampleDataA> referenceAttribute =new DataReferenceAttribute<>(ExampleDataA.class);
    }

    @Test
    public void testObservable(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        ArrayList<String> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(""));
        referenceAttribute.set(new ExampleDataA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceFactory);
    }

    @Test
    public void remove_Listener(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> invalidationListener = (a, o) -> {
            calls.add("");
        };
        referenceAttribute.internal_addListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());

        Assert.assertEquals(1,calls.size());

        referenceAttribute.internal_removeListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        Assert.assertNull(referenceAttribute.get());
        List<ExampleDataA> exampleFactoryAS = referenceAttribute.internal_createNewPossibleValues();
        referenceAttribute.set(exampleFactoryAS.get(0));
        Assert.assertNotNull(referenceAttribute.get());

    }

    @Test
    public void test_get_possible(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);

        ExampleReferenceFactory root = new ExampleReferenceFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        root.referenceAttribute.set(exampleFactoryA);

        referenceAttribute.internal_prepareUsage(root);

        Collection<ExampleDataA> possibleFactories =referenceAttribute.internal_possibleValues();
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA,new ArrayList<>(possibleFactories).get(0));

    }

    @Test
    public void test_Observable_first(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        ArrayList<Object> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(value));
        ExampleDataA added = new ExampleDataA();
        referenceAttribute.set(added);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals(added,calls.get(0));
    }

    @Test
    public void test_listener(){
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);

        List<ExampleDataA> calls = new ArrayList<>();
        List<ExampleDataA> callsAttributeGet = new ArrayList<>();
        referenceAttribute.internal_addListener((attribute, value) -> {
            calls.add(value);
            callsAttributeGet.add(attribute.get());
        });
        ExampleDataA exampleFactoryA = new ExampleDataA();
        referenceAttribute.set(exampleFactoryA);
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals(exampleFactoryA,calls.get(0));
        Assert.assertEquals(exampleFactoryA,callsAttributeGet.get(0));

    }

    @Test
    public void removeListener() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void delegate_root_for_added() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);
        attribute.internal_prepareUsage(new ExampleDataB());

        final ExampleDataA exampleFactoryA = new ExampleDataA();
        Assert.assertFalse(exampleFactoryA.internal().readyForUsage());
        attribute.set(exampleFactoryA);
        Assert.assertTrue(exampleFactoryA.internal().readyForUsage());
    }


    @Test
    public void test_semanticcopy_self(){
        DataReferenceAttribute<ExampleDataA> attributeFrom =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.set(new ExampleDataA());
        DataReferenceAttribute<ExampleDataA> attributeTo =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assert.assertTrue("same reference",attributeFrom.get()==attributeTo.get());
    }

    @Test
    public void test_semanticcopy_copy(){
        DataReferenceAttribute<ExampleDataA> attributeFrom =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.set(new ExampleDataA());
        DataReferenceAttribute<ExampleDataA> attributeTo =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assert.assertTrue("not same reference",attributeFrom.get()!=attributeTo.get());
        Assert.assertNotEquals(attributeFrom.get().getId(),attributeTo.get().getId());
    }


}