package de.factoryfx.adminui.angularjs.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import de.factoryfx.adminui.InMemoryFactoryStorage;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.adminui.angularjs.integration.example.ExampleLiveObjectA;
import de.factoryfx.adminui.angularjs.integration.example.ExampleVisitor;
import de.factoryfx.adminui.angularjs.model.FactoryTypeInfoWrapper;
import de.factoryfx.adminui.angularjs.model.WebGuiUser;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.NoUserManagement;
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

        ApplicationServer<ExampleLiveObjectA, ExampleVisitor, ExampleFactoryA> defaultApplicationServer = new ApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
        defaultApplicationServer.start();

        RestResource<ExampleLiveObjectA, ExampleVisitor,ExampleFactoryA> restResource = new RestResource<>(layout,
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


        ExampleFactoryA update = exampleFactoryA.copy();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();

        update.referenceListAttribute.add(exampleFactoryB);
        restResource.save(new FactoryTypeInfoWrapper(update));


        exampleFactoryB.referenceAttribute.set(shared.copy());
        restResource.save(new FactoryTypeInfoWrapper(update));


        restResource.deploy();


    }

}