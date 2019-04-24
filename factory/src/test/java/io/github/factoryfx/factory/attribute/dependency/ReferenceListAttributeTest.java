package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReferenceListAttributeTest {

    public static class ExampleReferenceListFactory extends FactoryBase<Void,ExampleDataA> {
        public FactoryListAttribute<ExampleDataA,Void,ExampleDataA> referenceListAttribute =new FactoryListAttribute<>();


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
        AttributeChangeListener<List<ExampleDataA>, FactoryListAttribute<ExampleDataA,Void,ExampleDataA>> invalidationListener = (a, o) -> {
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
        ExampleReferenceListFactory factory =new ExampleReferenceListFactory();
        factory.internal().addBackReferences();

        factory.referenceListAttribute.internal_addBackReferences(new ExampleDataA(),null);
        List<ExampleDataA> exampleDataAS = factory.referenceListAttribute.internal_createNewPossibleValues();
        factory.referenceListAttribute.add(exampleDataAS.get(0));
        Assertions.assertEquals(1,factory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        UUID expectedId=exampleFactoryA.getId();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        exampleReferenceListFactory = exampleReferenceListFactory.internal().addBackReferences();


        Collection<ExampleDataA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.internal_possibleValues();
        Assertions.assertEquals(1,possibleFactories.size());
        Assertions.assertEquals(expectedId,new ArrayList<>(possibleFactories).get(0).getId());

    }

    @Test
    public void test_add_new_listener(){
        ExampleReferenceListFactory factory =new ExampleReferenceListFactory();
        factory.internal().addBackReferences();
        List<ExampleDataA> calls=new ArrayList<>();
        factory.referenceListAttribute.internal_addListener((attribute, value) -> calls.add(value.get(0)));

        factory.referenceListAttribute.add(factory.referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assertions.assertEquals(1,calls.size());

        factory.referenceListAttribute.add(factory.referenceListAttribute.internal_createNewPossibleValues().get(0));

        Assertions.assertEquals(2,calls.size());
//        Assertions.assertEquals(value,calls.get(0));
    }

    @Test
    public void removeListener() {
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute =new FactoryListAttribute<>();

        final AttributeChangeListener<List<ExampleDataA>, FactoryListAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertEquals(1,attribute.internal_getListeners().size());
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener() {
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute =new FactoryListAttribute<>();

        final AttributeChangeListener<List<ExampleDataA>, FactoryListAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.internal_getListeners().size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() {
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute =new FactoryListAttribute<>();

        final AttributeChangeListener<List<ExampleDataA>, FactoryListAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));//null to simulate garbage collected weakref
        Assertions.assertTrue(attribute.internal_getListeners().size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
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
            FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute = new FactoryListAttribute<>();
            attribute.internal_addBackReferences(new ExampleDataA(), null);
            attribute.get().add(null);
        });
    }

    @Test
    public void delegate_root_for_addAll_with_null_nested(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute = new FactoryListAttribute<>();
            attribute.internal_addBackReferences(new ExampleDataA(), null);

            final ExampleDataA exampleFactoryA1 = new ExampleDataA();
            exampleFactoryA1.referenceListAttribute.add(null);
            attribute.get().add(exampleFactoryA1);
            Assertions.assertTrue(exampleFactoryA1.internal().readyForUsage());
        });
    }

    @Test
    public void test_semanticcopy_self(){
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attributeFrom =new FactoryListAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.add(new ExampleDataA());
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attributeTo =new FactoryListAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get(0)==attributeTo.get(0),"same reference");
    }

    @Test
    public void test_semanticcopy_copy(){
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attributeFrom =new FactoryListAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.add(new ExampleDataA());
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attributeTo =new FactoryListAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get(0)!=attributeTo.get(0),"not same reference");
        Assertions.assertNotEquals(attributeFrom.get(0).getId(),attributeTo.get(0).getId());
    }

    @Test
    public void test_sort_notifies_listener(){
        FactoryListAttribute<ExampleDataA,Void,ExampleDataA> attribute =new FactoryListAttribute<>();
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