package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.function.Function;

import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ViewReferenceAttributeTest {

    public static class ViewExampleFactory extends IdData{

        public final ViewReferenceAttribute<ViewExampleFactoryRoot,ExampleFactoryA> view= new ViewReferenceAttribute<>(new AttributeMetadata(), new Function<ViewExampleFactoryRoot, ExampleFactoryA>() {
            @Override
            public ExampleFactoryA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                if (include.get()){
                    return viewExampleFactoryRoot.exampleFactoryA.get();
                }
                return null;
            }
        });

        public final BooleanAttribute include= new BooleanAttribute(new AttributeMetadata());
    }

    public static class ViewExampleFactoryRoot extends IdData{
        public final ReferenceAttribute<ViewExampleFactory> ref = new ReferenceAttribute<>(ViewExampleFactory.class,new AttributeMetadata());
        public final ReferenceAttribute<ExampleFactoryA> exampleFactoryA= new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

    }


    @Test
    public void test(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleFactoryA value = new ExampleFactoryA();
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
        ExampleFactoryA value = new ExampleFactoryA();
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
        ExampleFactoryA value = new ExampleFactoryA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root = root.internal().prepareUsableCopy();
        root.ref.get().view.setRunlaterExecutorForTest(runnable -> runnable.run());

        ArrayList<String> calls=new ArrayList<>();
        root.ref.get().view.addListener((attribute, value1) -> {
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
        ExampleFactoryA value = new ExampleFactoryA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root.internal().prepareUsableCopy();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.addListener((attribute, value1) -> calls.add(value1.stringAttribute.get()));

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
        ExampleFactoryA value = new ExampleFactoryA();
        root.exampleFactoryA.set(value);

        root.internal().copy();

    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleFactoryA value = new ExampleFactoryA();
        root.exampleFactoryA.set(value);

        root.internal().prepareUsableCopy();
    }

    @Test
    public void removeListener() throws Exception {
        ViewReferenceAttribute<ViewExampleFactoryRoot,ExampleFactoryA> attribute= new ViewReferenceAttribute<>(new AttributeMetadata(), new Function<ViewExampleFactoryRoot, ExampleFactoryA>() {
            @Override
            public ExampleFactoryA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                return viewExampleFactoryRoot.exampleFactoryA.get();
            }
        });

        final AttributeChangeListener<ExampleFactoryA> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.addListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        ViewReferenceAttribute<ViewExampleFactoryRoot,ExampleFactoryA> attribute= new ViewReferenceAttribute<>(new AttributeMetadata(), new Function<ViewExampleFactoryRoot, ExampleFactoryA>() {
            @Override
            public ExampleFactoryA apply(ViewExampleFactoryRoot viewExampleFactoryRoot) {
                 return viewExampleFactoryRoot.exampleFactoryA.get();
            }
        });

        final AttributeChangeListener<ExampleFactoryA> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }



}