package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class ViewReferenceAttributeTest {

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

        root = root.internal().prepareUsableCopy();

        Assert.assertEquals(value.getId(),root.ref.get().view.get().getId());
    }

    @Test
    public void test_null(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().prepareUsableCopy();

        Assert.assertEquals(null,viewExampleFactory.view.get());
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

        root = root.internal().prepareUsableCopy();
        root.ref.get().view.setRunlaterExecutorForTest(runnable -> runnable.run());

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

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("123",calls.get(0));


        calls.clear();
        root.ref.get().include.set(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("null",calls.get(0));
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

        root.internal().prepareUsableCopy();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.internal_addListener((attribute, value1) -> calls.add(value1.stringAttribute.get()));

        viewExampleFactory.include.set(false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(0,calls.size());
    }

    @Test
    public void test_copy(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().copy();

    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleDataA value = new ExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().prepareUsableCopy();
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
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA> attribute= new DataViewReferenceAttribute<>(new Function<ViewExampleFactoryRoot, ExampleDataA>() {
            @Override
            public ExampleDataA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                 return viewExampleFactoryRoot.exampleFactoryA.get();
            }
        });

        final AttributeChangeListener<ExampleDataA,DataViewReferenceAttribute<ViewExampleFactoryRoot,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }



}