package de.factoryfx.server;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationServerAwareFactoryTest {

    @Test
    public void test(){
        final RootTestclazz rootTestclazz = new RootTestclazz();
        final ApplicationServerAwareFactoryTestclazz value = new ApplicationServerAwareFactoryTestclazz();
        rootTestclazz.ref.set(value);
        Assert.assertNull(value.applicationServer.get());

        ApplicationServer<String,Void,RootTestclazz> applicationServer = new ApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(rootTestclazz));
        applicationServer.start();

        Assert.assertEquals(applicationServer,applicationServer.getCurrentFactory().ref.get().applicationServer.get());

    }

    public static class ApplicationServerAwareFactoryTestclazz extends ApplicationServerAwareFactory<Void, String, RootTestclazz, String>{

    }

    public static class RootTestclazz extends SimpleFactoryBase<String,Void>{

        public final FactoryReferenceAttribute<String,ApplicationServerAwareFactoryTestclazz> ref = new FactoryReferenceAttribute<>(ApplicationServerAwareFactoryTestclazz.class,new AttributeMetadata());

        @Override
        public String createImpl() {
            return "";
        }
    }
}