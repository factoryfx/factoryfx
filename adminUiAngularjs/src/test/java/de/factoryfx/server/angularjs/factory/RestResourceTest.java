package de.factoryfx.server.angularjs.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.server.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.server.angularjs.integration.example.ExampleLiveObjectA;
import de.factoryfx.server.angularjs.integration.example.ExampleVisitor;
import de.factoryfx.server.angularjs.model.FactoryTypeInfoWrapper;
import de.factoryfx.server.angularjs.model.WebGuiUser;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.nop.NoUserManagement;
import org.junit.Test;

public class RestResourceTest {

    @Test
    public void test_add_new_with_reference(){
        Layout layout = new Layout(new HashMap<>(), null, null, false);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB existingListEntry = new ExampleFactoryB();
        ExampleFactoryA shared = new ExampleFactoryA();
        existingListEntry.referenceAttribute.set(shared);
        exampleFactoryA.referenceListAttribute.add(existingListEntry);
        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        shared=exampleFactoryA.referenceListAttribute.get().get(0).referenceAttribute.get();

        ApplicationServer<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA,Void> defaultApplicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), new InMemoryDataStorage<>(exampleFactoryA));
        defaultApplicationServer.start();

        RestResource<ExampleVisitor,ExampleLiveObjectA,ExampleFactoryA,Void> restResource = new RestResource<>(layout,
                defaultApplicationServer,
                Arrays.asList(ExampleFactoryA.class, ExampleFactoryB.class),
                Arrays.asList(Locale.ENGLISH),
                new NoUserManagement(),
                null,
                null,
                null,
                new SessionStorageMock());

        WebGuiUser user = new WebGuiUser();
        user.user="sfsgfdgfdfgf";
        restResource.login(user);
        restResource.init();


        ExampleFactoryA update = exampleFactoryA.internal().copy();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();

        update.referenceListAttribute.add(exampleFactoryB);
        restResource.save(new FactoryTypeInfoWrapper(update));


        exampleFactoryB.referenceAttribute.set(shared.internal().copy());
        restResource.save(new FactoryTypeInfoWrapper(update));


        restResource.deploy();


    }

}