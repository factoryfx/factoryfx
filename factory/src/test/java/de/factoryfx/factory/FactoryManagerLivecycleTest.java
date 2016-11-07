package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerLivecycleTest {

    public static class  ExampleLiveObjectA{
        public final String string;
        public final ExampleLiveObjectA ref;

        public ExampleLiveObjectA(String string, ExampleLiveObjectA ref) {
            this.string = string;
            this.ref = ref;
        }
    }


    public static class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,Void> {

        private final LiveCycleControllerCallCounter liveCycleController;
        public ExampleFactoryA(LiveCycleControllerCallCounter liveCycleController){
            this.liveCycleController=liveCycleController;
            config().setNewInstanceSupplier(()-> new ExampleFactoryA(liveCycleController));
        }

        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
        public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));

        @Override
        public LiveCycleController<ExampleLiveObjectA, Void> createLifecycleController() {
            liveCycleController.exampleFactoryA=this;
            return liveCycleController;
        }
    }

    public static class ExampleFactoryB extends FactoryBase<ExampleLiveObjectA,Void> {

        private final LiveCycleController<ExampleLiveObjectA, Void> liveCycleController;
        public ExampleFactoryB(LiveCycleController<ExampleLiveObjectA, Void> liveCycleController){
            this.liveCycleController=liveCycleController;
            config().setNewInstanceSupplier(()-> new ExampleFactoryB(liveCycleController));
        }

        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

        @Override
        public LiveCycleController<ExampleLiveObjectA, Void> createLifecycleController() {
            return liveCycleController;
        }

    }

    public static class LiveCycleControllerCallCounter implements LiveCycleController<ExampleLiveObjectA, Void>{

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        private final String callId;
        private final Function<ExampleFactoryA,ExampleLiveObjectA> creator;

        public ExampleFactoryA exampleFactoryA;
        public LiveCycleControllerCallCounter(String callId, Function<ExampleFactoryA,ExampleLiveObjectA>   creator){
            this.callId=callId;
            this.creator=creator;
        }

        @Override
        public ExampleLiveObjectA create() {
            createCalls.add(callId);
            return creator.apply(exampleFactoryA);
        }

        @Override
        public ExampleLiveObjectA reCreate(ExampleLiveObjectA previousLiveObject) {
            reCreateCalls.add(callId);
            return creator.apply(exampleFactoryA);
        }

        @Override
        public void start(ExampleLiveObjectA newLiveObject) {
            startCalls.add(callId);
        }

        @Override
        public void destroy(ExampleLiveObjectA previousLiveObject) {
            destroyCalls.add(callId);
        }

        public void reset(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }

    @Test
    public void test_initial_start(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        LiveCycleControllerCallCounter liveCycleControllerA = new LiveCycleControllerCallCounter("A", exampleFactoryA -> new ExampleLiveObjectA("",exampleFactoryA.referenceAttribute.instance()));
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA(liveCycleControllerA);

        LiveCycleControllerCallCounter liveCycleControllerB = new LiveCycleControllerCallCounter("B", exampleFactorysdfg -> new ExampleLiveObjectA("",null));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB(liveCycleControllerB);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        Assert.assertEquals(1,liveCycleControllerA.createCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.createCalls.size());

        Assert.assertEquals(1,liveCycleControllerA.startCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.startCalls.size());
    }

    @Test
    public void test_initial_destroy(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        LiveCycleControllerCallCounter liveCycleControllerA = new LiveCycleControllerCallCounter("A", exampleFactoryA -> new ExampleLiveObjectA("",exampleFactoryA.referenceAttribute.instance()));
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA(liveCycleControllerA);

        LiveCycleControllerCallCounter liveCycleControllerB = new LiveCycleControllerCallCounter("B", exampleFactorysdfg -> new ExampleLiveObjectA("",null));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB(liveCycleControllerB);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);
        factoryManager.stop();

        Assert.assertEquals(1,liveCycleControllerA.destroyCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.destroyCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        LiveCycleControllerCallCounter liveCycleControllerA = new LiveCycleControllerCallCounter("A", exampleFactoryA -> new ExampleLiveObjectA("",exampleFactoryA.referenceAttribute.instance()));
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA(liveCycleControllerA);

        LiveCycleControllerCallCounter liveCycleControllerB = new LiveCycleControllerCallCounter("B", exampleFactorysdfg -> new ExampleLiveObjectA("",null));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB(liveCycleControllerB);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.referenceAttribute.get().stringAttribute.set("changed");

        liveCycleControllerA.reset();
        liveCycleControllerB.reset();
        factoryManager.update(common,update);

        Assert.assertEquals(1,liveCycleControllerA.destroyCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.destroyCalls.size());

        Assert.assertEquals(1,liveCycleControllerA.reCreateCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.reCreateCalls.size());

        Assert.assertEquals(1,liveCycleControllerA.startCalls.size());
        Assert.assertEquals(1,liveCycleControllerB.startCalls.size());
    }

}