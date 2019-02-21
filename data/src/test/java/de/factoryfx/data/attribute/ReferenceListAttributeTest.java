package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals(1,calls.size());
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

        Assertions.assertEquals(1,calls.size());

        exampleReferenceListFactory.referenceListAttribute.internal_removeListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleDataA());
        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        exampleReferenceListFactory.referenceListAttribute.internal_addBackReferences(new ExampleReferenceListFactory(),null);
        List<ExampleDataA> exampleDataAS = exampleReferenceListFactory.referenceListAttribute.internal_createNewPossibleValues();
        exampleReferenceListFactory.referenceListAttribute.add(exampleDataAS.get(0));
        Assertions.assertEquals(1,exampleReferenceListFactory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        String expectedId=exampleFactoryA.getId();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        exampleReferenceListFactory = exampleReferenceListFactory.internal().addBackReferences();


        Collection<ExampleDataA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.internal_possibleValues();
        Assertions.assertEquals(1,possibleFactories.size());
        Assertions.assertEquals(expectedId,new ArrayList<>(possibleFactories).get(0).getId());

    }

    @Test
    public void test_add_new_listener(){
        DataReferenceListAttribute<ExampleDataA> referenceListAttribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        referenceListAttribute.internal_addBackReferences(new ExampleDataA(),null);
        List<ExampleDataA> calls=new ArrayList<>();
        referenceListAttribute.internal_addListener((attribute, value) -> calls.add(value.get(0)));

        referenceListAttribute.add(referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assertions.assertEquals(1,calls.size());

        referenceListAttribute.add(referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assertions.assertEquals(2,calls.size());
//        Assertions.assertEquals(value,calls.get(0));
    }

    @Test
    public void removeListener() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertEquals(1,attribute.listeners.size());
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<List<ExampleDataA>,DataReferenceListAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));//null to simulate garbage collected weakref
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void delegate_root_for_added_ref() {
        final ExampleDataA root = new ExampleDataA();
        root.internal().addBackReferences();

        ExampleDataB value = new ExampleDataB();
        Assertions.assertFalse(value.internal().readyForUsage());
        root.referenceAttribute.set(value);
        Assertions.assertTrue(value.internal().readyForUsage());
    }

    @Test
    public void delegate_root_for_added_refList() {
        final ExampleDataA root = new ExampleDataA();
        root.internal().addBackReferences();

        ExampleDataB value = new ExampleDataB();
        Assertions.assertFalse(value.internal().readyForUsage());
        root.referenceListAttribute.add(value);
        Assertions.assertTrue(value.internal().readyForUsage());
    }

    @Test
    public void delegate_root_for_addAll_with_null(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DataReferenceListAttribute<ExampleDataA> attribute = new DataReferenceListAttribute<>(ExampleDataA.class);
            attribute.internal_addBackReferences(new ExampleDataB(), null);
            attribute.get().add(null);
        });
    }

    @Test
    public void delegate_root_for_addAll_with_null_nested(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DataReferenceListAttribute<ExampleDataA> attribute = new DataReferenceListAttribute<>(ExampleDataA.class);
            attribute.internal_addBackReferences(new ExampleDataB(), null);

            final ExampleDataA exampleFactoryA1 = new ExampleDataA();
            exampleFactoryA1.referenceListAttribute.add(null);
            attribute.get().add(exampleFactoryA1);
            Assertions.assertTrue(exampleFactoryA1.internal().readyForUsage());
        });
    }

    @Test
    public void test_semanticcopy_self(){
        DataReferenceListAttribute<ExampleDataA> attributeFrom =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.add(new ExampleDataA());
        DataReferenceListAttribute<ExampleDataA> attributeTo =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get(0)==attributeTo.get(0),"same reference");
    }

    @Test
    public void test_semanticcopy_copy(){
        DataReferenceListAttribute<ExampleDataA> attributeFrom =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.add(new ExampleDataA());
        DataReferenceListAttribute<ExampleDataA> attributeTo =new DataReferenceListAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get(0)!=attributeTo.get(0),"not same reference");
        Assertions.assertNotEquals(attributeFrom.get(0).getId(),attributeTo.get(0).getId());
    }

    @Test
    public void test_sort_notifies_listener(){
        DataReferenceListAttribute<ExampleDataA> attribute =new DataReferenceListAttribute<>(ExampleDataA.class);
        {
            ExampleDataA exampleDataA = new ExampleDataA();
            exampleDataA.stringAttribute.set("a");
            attribute.add(exampleDataA);
        }
        {
            ExampleDataA exampleDataA = new ExampleDataA();
            exampleDataA.stringAttribute.set("c");
            attribute.add(exampleDataA);
        }
        {
            ExampleDataA exampleDataA = new ExampleDataA();
            exampleDataA.stringAttribute.set("b");
            attribute.add(exampleDataA);
        }

        int[] counter=new int[1];
        attribute.internal_addListener((attribute1, value) -> {
            counter[0]++;
        });

        Assertions.assertEquals(0,counter[0]);
        attribute.sort(Comparator.comparing(o -> o.stringAttribute.get()));
        Assertions.assertEquals(1,counter[0]);
    }

}