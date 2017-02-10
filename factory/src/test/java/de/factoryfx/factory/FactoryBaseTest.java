package de.factoryfx.factory;

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

    public static class ExampleFactoryAndViewRoot extends SimpleFactoryBase<Void,Void> {
        public final FactoryReferenceAttribute<Void,ExampleFactoryAndViewA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryAndViewA.class,new AttributeMetadata().labelText("ExampleA2"));
        public final FactoryReferenceAttribute<Void,XFactory> xFactory = new FactoryReferenceAttribute<>(XFactory.class,new AttributeMetadata().labelText("XFactory"));
        public final FactoryReferenceListAttribute<Void,XFactory> xFactoryList = new FactoryReferenceListAttribute<>(XFactory.class,new AttributeMetadata().labelText("XFactory"));

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<Void,Void> {
        public final FactoryViewReferenceAttribute<ExampleFactoryAndViewRoot,Void,XFactory> referenceView = new FactoryViewReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"),
                root -> root.xFactory.get());
        public final FactoryViewListReferenceAttribute<ExampleFactoryAndViewRoot,Void,XFactory> listView = new FactoryViewListReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"),
                root -> root.xFactoryList.get());

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class XFactory extends SimpleFactoryBase<Void,Void> {
        public final StringAttribute bla=new StringAttribute(new AttributeMetadata());
        @Override
        public Void createImpl() {
            return null;
        }
    }



    @Test
    public void test_changedDeep(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();

        usableCopy.internalFactory().markChanged();
        Assert.assertTrue(usableCopy.changedDeep());
    }

    @Test
    public void test_changedDeep_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();

        usableCopy.referenceAttribute.get().internalFactory().markChanged();
        Assert.assertTrue(usableCopy.changedDeep());
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();

        usableCopy.internalFactory().markChanged();
        Assert.assertFalse(usableCopy.referenceAttribute.get().changedDeep());
    }

    @Test
    public void test_changedDeep_changeded_view(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();

        usableCopy.xFactory.get().internalFactory().markChanged();
        Assert.assertTrue(usableCopy.referenceAttribute.get().changedDeep());
    }


    @Test
    public void test_changedDeep_viewlist(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.xFactoryList.add(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();

        usableCopy.xFactoryList.get().get(0).internalFactory().markChanged();
        Assert.assertTrue(usableCopy.referenceAttribute.get().changedDeep());
    }
}