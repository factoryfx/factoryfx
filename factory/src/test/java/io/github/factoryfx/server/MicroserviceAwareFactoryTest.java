package io.github.factoryfx.server;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import org.junit.jupiter.api.Test;

public class MicroserviceAwareFactoryTest {

    @Test
    public void test(){
        RootTestClazz rootTestclazz = new RootTestClazz();
        final MicroserviceAwareFactoryTestclazz value = new MicroserviceAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);

        FactoryTreeBuilder<String,RootTestClazz> builder = new FactoryTreeBuilder<>(RootTestClazz.class, context -> {
            return rootTestclazz;
        });
        Microservice<String,RootTestClazz> microservice = builder.microservice().build();
        microservice.start();

        //assert no npe in MicroserviceAwareFactoryTestclazz
    }

    public static class MicroserviceAwareFactoryTestclazz extends FactoryBase<String,RootTestClazz> {
        public MicroserviceAwareFactoryTestclazz(){
            this.configLifeCycle().setCreator(() -> "");
        }
    }

    public static class RootTestClazz extends SimpleFactoryBase<String,RootTestClazz> {

        public final FactoryAttribute<String,MicroserviceAwareFactoryTestclazz> ref = new FactoryAttribute<>();

        @Override
        protected String createImpl() {
            this.utility().getMicroservice().prepareNewFactory(); //no npw, Microservice available

            return "";
        }
    }


}