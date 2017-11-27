package de.factoryfx.server;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationServerAwareFactoryTest {

    @Test
    public void test(){
        final RootTestClazz rootTestclazz = new RootTestClazz();
        final ApplicationServerAwareFactoryTestclazz value = new ApplicationServerAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);
        Assert.assertNull(value.applicationServer.get());

        final FactoryManager<Void, String, RootTestClazz> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<>());
        ApplicationServer<Void,String,RootTestClazz> applicationServer = new ApplicationServer<>(factoryManager, new InMemoryDataStorage<>(rootTestclazz));
        applicationServer.start();

        Assert.assertEquals(applicationServer,factoryManager.getCurrentFactory().ref.get().applicationServer.get());

    }

    public static class ApplicationServerAwareFactoryTestclazz extends ApplicationServerAwareFactory<Void, String, RootTestClazz, String>{
        public ApplicationServerAwareFactoryTestclazz(){
            this.configLiveCycle().setCreator(() -> "");
        }
    }

    public static class RootTestClazz extends SimpleFactoryBase<String,Void>{

        public final FactoryReferenceAttribute<String,ApplicationServerAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(ApplicationServerAwareFactoryTestclazz.class);

        @Override
        public String createImpl() {
            return "";
        }
    }


}