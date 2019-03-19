package io.github.factoryfx.server;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import org.junit.jupiter.api.Test;

public class MicroserviceAwareFactoryTest {

    @Test
    public void test(){
        RootTestClazz rootTestclazz = new RootTestClazz();
        final MicroserviceAwareFactoryTestclazz value = new MicroserviceAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);

        FactoryTreeBuilder<String,RootTestClazz,Void> builder = new FactoryTreeBuilder<>(RootTestClazz.class);
        builder.addFactory(RootTestClazz.class, Scope.SINGLETON, context -> {
            return rootTestclazz;
        });
        Microservice<String,RootTestClazz,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        //assert no npe in MicroserviceAwareFactoryTestclazz
    }

    public static class MicroserviceAwareFactoryTestclazz extends FactoryBase<String,RootTestClazz> {
        public MicroserviceAwareFactoryTestclazz(){
            this.configLifeCycle().setCreator(() -> "");
        }
    }

    public static class RootTestClazz extends SimpleFactoryBase<String,RootTestClazz> {

        public final FactoryReferenceAttribute<String,MicroserviceAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(MicroserviceAwareFactoryTestclazz.class);

        @Override
        public String createImpl() {
            this.utilityFactory().getMicroservice().prepareNewFactory(); //no npw, Microservice available

            return "";
        }
    }


}