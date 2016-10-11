package de.factoryfx.factory;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Ignore;
import org.junit.Test;

public class FactoryManagerLivecycleTest {


    private static class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,Void> {

        private final LiveCycleController<ExampleLiveObjectA, Void> liveCycleController;
        public ExampleFactoryA(LiveCycleController<ExampleLiveObjectA, Void> liveCycleController){
            this.liveCycleController=liveCycleController;
        }

        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));

        @Override
        public LiveCycleController<ExampleLiveObjectA, Void> createLifecycleController() {
            return liveCycleController;
        }
    }

    private static class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,Void> {

        private final LiveCycleController<ExampleLiveObjectB, Void> liveCycleController;
        public ExampleFactoryB(LiveCycleController<ExampleLiveObjectB, Void> liveCycleController){
            this.liveCycleController=liveCycleController;
        }

        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

        @Override
        public LiveCycleController<ExampleLiveObjectB, Void> createLifecycleController() {
            return liveCycleController;
        }
    }

    @Ignore
    @Test
    public void test_initial_start(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA(new LiveCycleController<ExampleLiveObjectA, Void>() {
            @Override
            public ExampleLiveObjectA create() {
                return null;
            }
        });
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB(null);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        exampleFactoryA.getCreatedLiveObject().isPresent();
        exampleFactoryB.getCreatedLiveObject().isPresent();
    }



}