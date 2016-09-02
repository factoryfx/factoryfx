package de.factoryfx.adminui.angularjs.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import de.factoryfx.adminui.InMemoryFactoryStorage;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.adminui.angularjs.integration.example.ExampleVisitor;
import de.factoryfx.adminui.angularjs.model.FactoryTypeInfoWrapper;
import de.factoryfx.adminui.angularjs.model.WebGuiUser;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.NoUserManagement;
import org.junit.Test;

public class WebGuiResourceTest {

    @Test
    public void test_add_new_with_reference(){
        WebGuiLayout webGuiLayout = new WebGuiLayout(new HashMap<>(), null, null, false);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB existingListEntry = new ExampleFactoryB();
        ExampleFactoryA shared = new ExampleFactoryA();
        existingListEntry.referenceAttribute.set(shared);
        exampleFactoryA.referenceListAttribute.add(existingListEntry);

        ApplicationServer<ExampleVisitor, ExampleFactoryA> defaultApplicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
        defaultApplicationServer.start();

        WebGuiResource<ExampleVisitor,ExampleFactoryA> webGuiResource= new WebGuiResource<>(
                webGuiLayout,
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
        webGuiResource.login(user);
        webGuiResource.init();


        ExampleFactoryA update = exampleFactoryA.copy();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();

        update.referenceListAttribute.add(exampleFactoryB);
        webGuiResource.save(new FactoryTypeInfoWrapper(update));


        exampleFactoryB.referenceAttribute.set(shared.copy());
        webGuiResource.save(new FactoryTypeInfoWrapper(update));


        webGuiResource.deploy();


    }

}