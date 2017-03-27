package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class FactoryBaseTest {

    @Test(expected = IllegalStateException.class)
    public void create_loop_test(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internalFactory().loopDetector();
    }

    @Test
    public void test_collect_Live_Objects(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internalFactory().instance();

        //TODO fix test
//        HashMap<String, LiveObject> liveObjects = new HashMap<>();
//        exampleFactoryA.collectLiveObjects(liveObjects);
//
//        Assert.assertEquals(3,liveObjects.entrySet().size());
    }

    public static class XRoot extends SimpleFactoryBase<String,Void> {
        public final FactoryReferenceAttribute<String,ExampleFactoryAndViewA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryAndViewA.class,new AttributeMetadata().labelText("ExampleA2"));
        public final FactoryReferenceAttribute<String,XFactory> xFactory = new FactoryReferenceAttribute<>(XFactory.class,new AttributeMetadata().labelText("XFactory"));
        public final FactoryReferenceAttribute<String,XFactory> xFactory2 = new FactoryReferenceAttribute<>(XFactory.class,new AttributeMetadata().labelText("XFactory"));
        public final FactoryReferenceListAttribute<String,XFactory> xFactoryList = new FactoryReferenceListAttribute<>(XFactory.class,new AttributeMetadata().labelText("XFactory"));

        @Override
        public String createImpl() {
            referenceAttribute.instance();
            xFactory.instance();
            xFactory2.instance();
            xFactoryList.instances();
            return "1";
        }
    }

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<String,Void> {
        public final FactoryViewReferenceAttribute<XRoot,String,XFactory> referenceView = new FactoryViewReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"),
                root -> root.xFactory.get());
        public final FactoryViewListReferenceAttribute<XRoot,String,XFactory> listView = new FactoryViewListReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"),
                root -> root.xFactoryList.get());

        @Override
        public String createImpl() {
            referenceView.instance();
            listView.instances();
            return "2";
        }
    }


    public static class XFactory extends SimpleFactoryBase<String,Void> {
        public final StringAttribute bla=new StringAttribute(new AttributeMetadata());

        public List<String> createCalls=new ArrayList<>();

        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }
    }



    @Test
    public void test_determineRecreationNeed(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();


        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy);
        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertFalse(usableCopy.xFactory.get().needRecreation);
        Assert.assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_determineRecreationNeed_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.referenceAttribute.get());
        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertFalse(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.referenceAttribute.get());
        changed.add(usableCopy.xFactory.get());

        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_changed_view(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.xFactory.get());

        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_changedDeep_viewlist(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactoryList.add(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.xFactoryList.get().get(0));

        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactoryList.get(0).needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_determineRecreationNeed_recreate_only_once(){
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.xFactory2.set(root.xFactory.get());

        final XRoot usableCopy = root.internal().prepareUsableCopy();
        usableCopy.internalFactory().instance();


        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.xFactory.get());
        usableCopy.internalFactory().determineRecreationNeed(changed,new HashSet<>());
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);

        usableCopy.xFactory.get().createCalls.clear();
        usableCopy.internalFactory().instance();
        Assert.assertEquals(1,usableCopy.xFactory.get().createCalls.size());

    }
}