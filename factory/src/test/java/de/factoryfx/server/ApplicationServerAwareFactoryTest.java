package de.factoryfx.server;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationServerAwareFactoryTest {

    @Test//1344
    public void test(){
        RootTestClazz rootTestclazz = new RootTestClazz();
        final ApplicationServerAwareFactoryTestclazz value = new ApplicationServerAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);
        rootTestclazz = rootTestclazz.internal().prepareUsableCopy();
//        Assert.assertNull(rootTestclazz.ref.get().utilityFactory().getApplicationServer());

        final FactoryManager<Void, RootTestClazz> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void, RootTestClazz>());
        ApplicationServer<Void,RootTestClazz,Void> applicationServer = new ApplicationServer<>(factoryManager, new InMemoryDataStorage<>(rootTestclazz));
        applicationServer.start();

        Assert.assertEquals(applicationServer,factoryManager.getCurrentFactory().ref.get().utilityFactory().getApplicationServer());

    }

    public static class ApplicationServerAwareFactoryTestclazz extends FactoryBase<String,Void,RootTestClazz> {
        public ApplicationServerAwareFactoryTestclazz(){
            this.configLiveCycle().setCreator(() -> "");
        }
    }

    public static class RootTestClazz extends SimpleFactoryBase<String,Void,RootTestClazz> {

        public final FactoryReferenceAttribute<String,ApplicationServerAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(ApplicationServerAwareFactoryTestclazz.class);

        @Override
        public String createImpl() {
            return "";
        }
    }


}