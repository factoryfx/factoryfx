package de.factoryfx.data.attribute;

import java.util.ArrayList;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ViewListReferenceAttributeTest {
    public class ViewListExampleFactory extends IdData {

        public final ViewListReferenceAttribute<ViewListExampleFactoryRoot,ViewListExampleFactory,ExampleFactoryA> view= new ViewListReferenceAttribute<>(new AttributeMetadata(), (ViewListExampleFactoryRoot viewExampleFactoryRoot, ViewListExampleFactory viewListExampleFactory)->{
                return viewExampleFactoryRoot.list.get().filtered(exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals(viewListExampleFactory.forFilter.get()));
            }
        );

        public final StringAttribute forFilter= new StringAttribute(new AttributeMetadata());
    }

    public class ViewListExampleFactoryRoot extends IdData{
        public final ReferenceAttribute<ViewListExampleFactory> ref = new ReferenceAttribute<>(ViewListExampleFactory.class,new AttributeMetadata());
        public final ReferenceListAttribute<ExampleFactoryA> list= new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
    }


    @Test
    public void test(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }

        root.internal().prepareRootEditing();

        Assert.assertEquals(1,viewExampleFactory.view.get().size());
        Assert.assertEquals("1",viewExampleFactory.view.get().get(0).stringAttribute.get());
    }

    @Test
    public void test_nomatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().prepareRootEditing();

        Assert.assertEquals(0,viewExampleFactory.view.get().size());
    }

    @Test
    public void test_multimatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }

        root.internal().prepareRootEditing();

        Assert.assertEquals(2,viewExampleFactory.view.get().size());
    }


    @Test
    public void test_change_listener(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().prepareRootEditing();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.addListener((attribute, value1) -> {
            if (!value1.isEmpty()){
                calls.add("1");
            } else {
                calls.add("empty");
            }
        });

        viewExampleFactory.forFilter.set("2");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(1,viewExampleFactory.view.get().size());
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("1",calls.get(0));


        calls.clear();
        viewExampleFactory.forFilter.set("1");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("empty",calls.get(0));
    }


    @Test
    public void test_change_listener_no_changes(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleFactoryA value = new ExampleFactoryA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().prepareRootEditing();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.addListener((attribute, value1) -> calls.add("1"));

        viewExampleFactory.forFilter.set("1");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(0,calls.size());
    }

}