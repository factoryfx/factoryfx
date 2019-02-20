package de.factoryfx.server;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import org.junit.Test;

public class MicroserviceAwareFactoryTest {

    @Test
    public void test(){
        RootTestClazz rootTestclazz = new RootTestClazz();
        final MicroserviceAwareFactoryTestclazz value = new MicroserviceAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);

        FactoryTreeBuilder<Void,String,RootTestClazz,Void> builder = new FactoryTreeBuilder<>(RootTestClazz.class);
        builder.addFactory(RootTestClazz.class, Scope.SINGLETON, context -> {
            return rootTestclazz;
        });
        Microservice<Void,String,RootTestClazz,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        //assert no npe in MicroserviceAwareFactoryTestclazz
    }

    public static class MicroserviceAwareFactoryTestclazz extends FactoryBase<String,Void,RootTestClazz> {
        public MicroserviceAwareFactoryTestclazz(){
            this.configLifeCycle().setCreator(() -> "");
        }
    }

    public static class RootTestClazz extends SimpleFactoryBase<String,Void,RootTestClazz> {

        public final FactoryReferenceAttribute<String,MicroserviceAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(MicroserviceAwareFactoryTestclazz.class);

        @Override
        public String createImpl() {
            this.utilityFactory().getMicroservice().prepareNewFactory(); //no npw, Microservice available

            return "";
        }
    }


}