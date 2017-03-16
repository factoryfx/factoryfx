package de.factoryfx.server;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationServerAwareFactoryTest {

    @Test
    public void test(){
        final RootTestclazz rootTestclazz = new RootTestclazz();
        final ApplicationServerAwareFactoryTestclazz value = new ApplicationServerAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);
        Assert.assertNull(value.applicationServer.get());

        final FactoryManager<String, Void, RootTestclazz> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<>());
        ApplicationServer<String,Void,RootTestclazz> applicationServer = new ApplicationServer<>(factoryManager, new InMemoryFactoryStorage<>(rootTestclazz));
        applicationServer.start();

        Assert.assertEquals(applicationServer,factoryManager.getCurrentFactory().ref.get().applicationServer.get());

    }

    public static class ApplicationServerAwareFactoryTestclazz extends ApplicationServerAwareFactory<Void, String, RootTestclazz, String>{
        public ApplicationServerAwareFactoryTestclazz(){
            this.configLiveCycle().setCreator(() -> "");
        }
    }

    public static class RootTestclazz extends SimpleFactoryBase<String,Void>{

        public final FactoryReferenceAttribute<String,ApplicationServerAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(ApplicationServerAwareFactoryTestclazz.class,new AttributeMetadata());

        @Override
        public String createImpl() {
            return "";
        }
    }
}