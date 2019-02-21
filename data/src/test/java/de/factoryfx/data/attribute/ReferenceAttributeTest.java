package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> invalidationListener = (a, o) -> {
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
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        Assertions.assertNull(referenceAttribute.get());
        List<ExampleDataA> exampleFactoryAS = referenceAttribute.internal_createNewPossibleValues();
        referenceAttribute.set(exampleFactoryAS.get(0));
        Assertions.assertNotNull(referenceAttribute.get());

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
        DataReferenceAttribute<ExampleDataA> referenceAttribute=new DataReferenceAttribute<>(ExampleDataA.class);
        ArrayList<Object> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(value));
        ExampleDataA added = new ExampleDataA();
        referenceAttribute.set(added);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(added,calls.get(0));
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
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(exampleFactoryA,calls.get(0));
        Assertions.assertEquals(exampleFactoryA,callsAttributeGet.get(0));

    }

    @Test
    public void removeListener() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        DataReferenceAttribute<ExampleDataA> attribute=new DataReferenceAttribute<>(ExampleDataA.class);

        final AttributeChangeListener<ExampleDataA,DataReferenceAttribute<ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void test_semanticcopy_self(){
        DataReferenceAttribute<ExampleDataA> attributeFrom =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.set(new ExampleDataA());
        DataReferenceAttribute<ExampleDataA> attributeTo =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()==attributeTo.get(),"same reference");
    }

    @Test
    public void test_semanticcopy_copy(){
        DataReferenceAttribute<ExampleDataA> attributeFrom =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.set(new ExampleDataA());
        DataReferenceAttribute<ExampleDataA> attributeTo =new DataReferenceAttribute<>(ExampleDataA.class);
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()!=attributeTo.get(),"not same reference");
        Assertions.assertNotEquals(attributeFrom.get().getId(),attributeTo.get().getId());
    }


}