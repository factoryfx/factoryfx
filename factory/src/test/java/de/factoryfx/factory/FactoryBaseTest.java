package de.factoryfx.factory;

import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
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

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<Void,Void> {
        public final FactoryViewReferenceAttribute<ExampleFactoryAndViewRoot,Void,ExampleFactoryAndViewA> referenceView = new FactoryViewReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"), new Function<ExampleFactoryAndViewRoot, ExampleFactoryAndViewA>() {
            @Override
            public ExampleFactoryAndViewA apply(ExampleFactoryAndViewRoot root) {
                return root.referenceAttribute.get();
            }
        });

        @Override
        public Void createImpl() {
            return null;
        }
    }




    @Test
    public void test_changedDeep(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();
        Assert.assertEquals(usableCopy.referenceAttribute.get(),usableCopy.referenceAttribute.get().referenceView.get());

        usableCopy.internalFactory().markChanged();
        Assert.assertTrue(usableCopy.changedDeep());
    }

    @Test
    public void test_changedDeep_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();
        Assert.assertEquals(usableCopy.referenceAttribute.get(),usableCopy.referenceAttribute.get().referenceView.get());

        usableCopy.referenceAttribute.get().internalFactory().markChanged();
        Assert.assertTrue(usableCopy.changedDeep());
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        ExampleFactoryAndViewRoot root = new ExampleFactoryAndViewRoot();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        final ExampleFactoryAndViewRoot usableCopy = root.internal().prepareUsableCopy();
        Assert.assertEquals(usableCopy.referenceAttribute.get(),usableCopy.referenceAttribute.get().referenceView.get());

        usableCopy.internalFactory().markChanged();
        Assert.assertTrue(usableCopy.referenceAttribute.get().changedDeep());
    }

}