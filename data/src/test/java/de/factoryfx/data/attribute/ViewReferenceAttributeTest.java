package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.function.BiFunction;

import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ViewReferenceAttributeTest {

    public class ViewExampleFactory extends IdData{

        public final ViewReferenceAttribute<ViewExampleFactoryRoot,ViewExampleFactory,ExampleFactoryA> view= new ViewReferenceAttribute<>(new AttributeMetadata(), new BiFunction<ViewExampleFactoryRoot, ViewExampleFactory, ExampleFactoryA>() {
            @Override
            public ExampleFactoryA apply(ViewExampleFactoryRoot viewExampleFactoryRoot, ViewExampleFactory viewExampleFactory) {
                if (viewExampleFactory.include.get()){
                    return viewExampleFactoryRoot.exampleFactoryA.get();
                }
                return null;
            }
        });

        public final BooleanAttribute include= new BooleanAttribute(new AttributeMetadata());
    }

    public class ViewExampleFactoryRoot extends IdData{
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

        root.prepareRootEditing();

        Assert.assertEquals(value,viewExampleFactory.view.get());
    }

    @Test
    public void test_null(){
        ViewExampleFactory viewExampleFactory=new ViewExampleFactory();
        viewExampleFactory.include.set(false);

        ViewExampleFactoryRoot root = new ViewExampleFactoryRoot();
        root.ref.set(viewExampleFactory);
        ExampleFactoryA value = new ExampleFactoryA();
        root.exampleFactoryA.set(value);

        root.prepareRootEditing();

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

        root.prepareRootEditing();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.addListener((attribute, value1) -> {
            if (value1!=null){
                calls.add(value1.stringAttribute.get());
            } else {
                calls.add("null");
            }
        });

        viewExampleFactory.include.set(true);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("123",calls.get(0));


        calls.clear();
        viewExampleFactory.include.set(false);
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

        root.prepareRootEditing();

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


}