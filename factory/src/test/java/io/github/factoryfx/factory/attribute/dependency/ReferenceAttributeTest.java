package io.github.factoryfx.factory.attribute.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends FactoryBase<Void,ExampleDataA> {
        public FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute =new FactoryAttribute<>();
    }

    @Test
    public void testObservable(){
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<String> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(""));
        referenceAttribute.set(new ExampleDataA());

        Assertions.assertEquals(1,calls.size());
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
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleDataA, FactoryAttribute<ExampleDataA,Void,ExampleDataA>> invalidationListener = (a, o) -> {
            calls.add("");
        };
        referenceAttribute.internal_addListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());

        Assertions.assertEquals(1,calls.size());

        referenceAttribute.internal_removeListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());
        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        exampleReferenceFactory.internal().addBackReferences();

        Assertions.assertNull(exampleReferenceFactory.referenceAttribute.get());
        List<ExampleDataA> exampleFactoryAS = exampleReferenceFactory.referenceAttribute.internal_createNewPossibleValues();
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryAS.get(0));
        Assertions.assertNotNull(exampleReferenceFactory.referenceAttribute.get());

    }

    @Test
    public void test_get_possible(){
        ExampleReferenceFactory root = new ExampleReferenceFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        root.referenceAttribute.set(exampleFactoryA);
        root.internal().addBackReferences();

        Collection<ExampleDataA> possibleFactories =root.referenceAttribute.internal_possibleValues();
        Assertions.assertEquals(1,possibleFactories.size());
        Assertions.assertEquals(exampleFactoryA,new ArrayList<>(possibleFactories).get(0));

    }

    @Test
    public void test_Observable_first(){
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<Object> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(value));
        ExampleDataA added = new ExampleDataA();
        referenceAttribute.set(added);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(added,calls.get(0));
    }

    @Test
    public void test_listener(){
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();

        List<ExampleDataA> calls = new ArrayList<>();
        List<ExampleDataA> callsAttributeGet = new ArrayList<>();
        referenceAttribute.internal_addListener((attribute, value) -> {
            calls.add(value);
            callsAttributeGet.add(attribute.get());
        });
        ExampleDataA exampleFactoryA = new ExampleDataA();
        referenceAttribute.set(exampleFactoryA);
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(exampleFactoryA,calls.get(0));
        Assertions.assertEquals(exampleFactoryA,callsAttributeGet.get(0));

    }

    @Test
    public void removeListener() throws Exception {
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<ExampleDataA,Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void test_semanticcopy_self(){
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attributeFrom =new FactoryAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.set(new ExampleDataA());
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attributeTo =new FactoryAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()==attributeTo.get(),"same reference");
    }

    @Test
    public void test_semanticcopy_copy(){
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attributeFrom =new FactoryAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.set(new ExampleDataA());
        FactoryAttribute<ExampleDataA,Void,ExampleDataA> attributeTo =new FactoryAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()!=attributeTo.get(),"not same reference");
        Assertions.assertNotEquals(attributeFrom.get().getId(),attributeTo.get().getId());
    }


}