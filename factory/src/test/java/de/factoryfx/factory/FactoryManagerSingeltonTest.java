package de.factoryfx.factory;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Test;

public class FactoryManagerSingeltonTest {

    public static class ExampleSingletonA extends FactoryBase<ExampleSingletonLiveObjectA,Void> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute1 = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute2 = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));


        @Override
        public LiveCycleController<ExampleSingletonLiveObjectA, Void> createLifecycleController() {
            return () -> new ExampleSingletonLiveObjectA(referenceAttribute1.instance(), referenceAttribute2.instance());
        }
    }

    static class ExampleSingletonLiveObjectA {
        private final ExampleLiveObjectB referenceAttribute1;
        private final ExampleLiveObjectB referenceAttribute2;
        public ExampleSingletonLiveObjectA(ExampleLiveObjectB referenceAttribute1, ExampleLiveObjectB referenceAttribute2) {

            this.referenceAttribute1 = referenceAttribute1;
            this.referenceAttribute2 = referenceAttribute2;
        }
    }

    @Test
    public void test_singleton(){
        FactoryManager<ExampleSingletonLiveObjectA,Void,ExampleSingletonA> factoryManager = new FactoryManager<>();

        ExampleSingletonA exampleSingletonA = new ExampleSingletonA();
        ExampleFactoryB value = new ExampleFactoryB();
        exampleSingletonA.referenceAttribute1.set(value);
        exampleSingletonA.referenceAttribute2.set(value);


        factoryManager.start(exampleSingletonA);
//
//        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
//        exampleSingletonA.collectLiveObjects(liveObjects);
//        Assert.assertEquals(2,liveObjects.size());

    }

    @Test
    public void test_multi_instances(){
        FactoryManager<ExampleSingletonLiveObjectA,Void,ExampleSingletonA> factoryManager = new FactoryManager<>();

        ExampleSingletonA exampleSingletonA = new ExampleSingletonA();
        exampleSingletonA.referenceAttribute1.set(new ExampleFactoryB());
        exampleSingletonA.referenceAttribute2.set(new ExampleFactoryB());


        factoryManager.start(exampleSingletonA);

        //TODO
//        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
//        exampleSingletonA.collectLiveObjects(liveObjects);
//        Assert.assertEquals(3,liveObjects.size());

    }


}