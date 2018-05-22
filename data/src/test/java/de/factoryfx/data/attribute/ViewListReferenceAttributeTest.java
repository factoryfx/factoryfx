package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class ViewListReferenceAttributeTest {
    public static class ViewListExampleFactory extends Data {

        public final StringAttribute forFilter= new StringAttribute();
        public final DataViewListReferenceAttribute<ViewListExampleFactoryRoot,ExampleDataA> view= new DataViewListReferenceAttribute<>( (ViewListExampleFactoryRoot viewExampleFactoryRoot)->{
                return viewExampleFactoryRoot.list.get().stream().filter(exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals(forFilter.get())).collect(Collectors.toList());
            }
        );

    }

    public static class ViewListExampleFactoryRoot extends Data{
        public final DataReferenceAttribute<ViewListExampleFactory> ref = new DataReferenceAttribute<>(ViewListExampleFactory.class);
        public final DataReferenceListAttribute<ExampleDataA> list= new DataReferenceListAttribute<>(ExampleDataA.class);
    }


    @Test
    public void test(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }

        root = root.internal().prepareUsableCopy();

        Assert.assertEquals(1,root.ref.get().view.get().size());
        Assert.assertEquals("1",root.ref.get().view.get().get(0).stringAttribute.get());
    }

    @Test
    public void test_json(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }

        root = root.internal().prepareUsableCopy();
        final String valueAsString = ObjectMapperBuilder.build().writeValueAsString(root);
        Assert.assertFalse(valueAsString.contains("view"));
    }

    @Test
    public void test_nomatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root = root.internal().prepareUsableCopy();

        Assert.assertEquals(0,root.ref.get().view.get().size());
    }

    @Test
    public void test_multimatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }

        root = root.internal().prepareUsableCopy();

        Assert.assertEquals(2,root.ref.get().view.get().size());
    }


    @Test
    public void test_change_listener(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root = root.internal().prepareUsableCopy();
        root.ref.get().view.setRunlaterExecutor(runnable -> runnable.run());

        ArrayList<String> calls=new ArrayList<>();
        root.ref.get().view.internal_addListener((attribute, value1) -> {
            if (!value1.isEmpty()){
                calls.add("1");
            } else {
                calls.add("empty");
            }
        });

        root.ref.get().forFilter.set("2");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(1,root.ref.get().view.get().size());
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("1",calls.get(0));


        calls.clear();
        root.ref.get().forFilter.set("1");
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
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().prepareUsableCopy();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.internal_addListener((attribute, value1) -> calls.add("1"));

        viewExampleFactory.forFilter.set("1");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(0,calls.size());
    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ExampleDataA value = new ExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().prepareUsableCopy();
    }

    @Test
    public void removeListener() throws Exception {
        DataViewListReferenceAttribute<ViewListExampleFactoryRoot, ExampleDataA> attribute = new DataViewListReferenceAttribute<>((ViewListExampleFactoryRoot viewExampleFactoryRoot) -> {
            return viewExampleFactoryRoot.list.filtered((exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals("")));
        });

        final AttributeChangeListener<List<ExampleDataA>,DataViewListReferenceAttribute<ViewListExampleFactoryRoot, ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        DataViewListReferenceAttribute<ViewListExampleFactoryRoot, ExampleDataA> attribute = new DataViewListReferenceAttribute<>((ViewListExampleFactoryRoot viewExampleFactoryRoot) -> {
            return viewExampleFactoryRoot.list.filtered((exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals("")));
        });

        final AttributeChangeListener<List<ExampleDataA>,DataViewListReferenceAttribute<ViewListExampleFactoryRoot, ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assert.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assert.assertTrue(attribute.listeners.size()==0);
    }
}
