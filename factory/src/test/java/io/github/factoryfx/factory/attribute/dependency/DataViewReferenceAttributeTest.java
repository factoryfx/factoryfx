package io.github.factoryfx.factory.attribute.dependency;

import java.util.ArrayList;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DataViewReferenceAttributeTest {

    public static class ViewExampleFactory extends FactoryBase<Void,ViewExampleFactoryRoot> {
        public final BooleanAttribute include= new BooleanAttribute();

        public final FactoryViewAttribute<ViewExampleFactoryRoot,Void,ViewExampleDataA> view= new FactoryViewAttribute<>((root) -> {
            if (include.get()) {
                return root.exampleFactoryA.get();
            }
            return null;
        });

    }

    public static class ViewExampleFactoryRoot extends FactoryBase<Void,ViewExampleFactoryRoot>{
        public final FactoryAttribute<Void,ViewExampleFactory> ref = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ViewExampleDataA> exampleFactoryA= new FactoryAttribute<>();

    }

    public static class ViewExampleDataA extends FactoryBase<Void, ViewExampleFactoryRoot> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    }


    @Test
    public void test(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ViewExampleDataA value = new ViewExampleDataA();
        root.exampleFactoryA.set(value);

        root = root.internal().finalise();

        Assertions.assertEquals(root.exampleFactoryA.get().getId(),root.ref.get().view.get().getId());
    }

    @Test
    public void test_null(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ViewExampleDataA value = new ViewExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().finalise();

        Assertions.assertEquals(null,viewExampleFactory.view.get());
    }

    @Test
    public void test_change_listener(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ViewExampleDataA value = new ViewExampleDataA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root = root.internal().finalise();
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
        ViewExampleDataA value = new ViewExampleDataA();
        value.stringAttribute.set("123");
        root.exampleFactoryA.set(value);

        root.internal().finalise();

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
        ViewExampleDataA value = new ViewExampleDataA();
        root.exampleFactoryA.set(value);
        root=root.internal().finalise();

        root.internal().copy();

    }

    @Test
    public void test_addBackReferences(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(true);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ViewExampleDataA value = new ViewExampleDataA();
        root.exampleFactoryA.set(value);

        root.internal().finalise();
        Assertions.assertEquals(root.ref.get().view.root,root);
    }

    @Test
    public void removeListener() throws Exception {
        FactoryViewAttribute<ViewExampleFactoryRoot,Void,ViewExampleDataA> attribute= new FactoryViewAttribute<>(viewExampleFactoryRoot -> {
            return viewExampleFactoryRoot.exampleFactoryA.get();
        });

        final AttributeChangeListener<ViewExampleDataA, FactoryViewAttribute<ViewExampleFactoryRoot,Void,ViewExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() {
        FactoryViewAttribute<ViewExampleFactoryRoot,Void,ViewExampleDataA> attribute= new FactoryViewAttribute<>(viewExampleFactoryRoot -> {
            return viewExampleFactoryRoot.exampleFactoryA.get();
        });

        final AttributeChangeListener<ViewExampleDataA, FactoryViewAttribute<ViewExampleFactoryRoot,Void,ViewExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
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
        ViewExampleDataA value = new ViewExampleDataA();
        root.exampleFactoryA.set(value);

        ObjectMapperBuilder.build().copy(root);
    }

}