package de.factoryfx.server;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Test;

public class MicroserviceAndTreeBuilderTest {

    @Test
    public void test_happy_case() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            return factory;
        });

        Microservice<ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
    }

}