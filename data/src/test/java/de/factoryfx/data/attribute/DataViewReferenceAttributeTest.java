package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataViewReferenceAttributeTest {

    public static class ViewExampleFactory extends Data {

        public final DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA> view= new DataViewReferenceAttribute<>(new Function<ViewExampleFactoryRoot, ExampleDataA>() {
            @Override
            public ExampleDataA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                if (include.get()){
                    return viewExampleFactoryRoot.exampleFactoryA.get();
                }
                return null;
            }
        });

        public final BooleanAttribute include= new BooleanAttribute();
    }

    public static class ViewExampleFactoryRoot extends Data{
        public final DataReferenceAttribute<ViewExampleFactory> ref = new DataReferenceAttribute<>(ViewExampleFactory.class);
        public final DataReferenceAttribute<ExampleDataA> exampleFactoryA= new DataReferenceAttribute<>(ExampleDataA.class);

    }


    @Test
    public void test(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root = root.internal().addBackReferences();

        Assertions.assertEquals(root.exampleFactoryA.get().getId(),root.ref.get().view.get().getId());
    }

    @Test
    public void test_null(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().addBackReferences();

        Assertions.assertEquals(null,viewExampleFactory.view.get());
    }

    @Test
    public void test_change_listener(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root = root.internal().addBackReferences();
        root.ref.get().view.setRunlaterExecutor(runnable -> runnable.run());

        ArrayList<String> calls=new ArrayList<>();
        root.ref.get().view.internal_addListener((attribute, value1) -> {
            if (value1!=null){
                calls.add(value1.stringAttribute.get());
            } else {
                calls.add("null");
            }
        });

        root.ref.get().include.set(true);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("123",calls.get(0));


        calls.clear();
        root.ref.get().include.set(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("null",calls.get(0));
    }


    @Test
    public void test_change_listener_no_changes(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root.internal().addBackReferences();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.internal_addListener((attribute, value1) -> calls.add(value1.stringAttribute.get()));

        viewExampleFactory.include.set(false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(0,calls.size());
    }

    @Test
    public void test_copy(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);
        root=root.internal().addBackReferences();

        root.internal().copy();

    }

    @Test
    public void test_addBackReferences(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().addBackReferences();
        Assertions.assertEquals(root.ref.get().view.root,root);
    }

    @Test
    public void removeListener() throws Exception {
        DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA> attribute= new DataViewReferenceAttribute<>(new Function<ViewExampleFactoryRoot, ExampleDataA>() {
            @Override
            public ExampleDataA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                return viewExampleFactoryRoot.exampleFactoryA.get();
            }
        });

        final AttributeChangeListener<ExampleDataA,DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() {
        DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA> attribute= new DataViewReferenceAttribute<>(new Function<ViewExampleFactoryRoot, ExampleDataA>() {
            @Override
            public ExampleDataA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                 return viewExampleFactoryRoot.exampleFactoryA.get();
            }
        });

        final AttributeChangeListener<ExampleDataA,DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void test_json() {
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        ObjectMapperBuilder.build().copy(root);
    }

}