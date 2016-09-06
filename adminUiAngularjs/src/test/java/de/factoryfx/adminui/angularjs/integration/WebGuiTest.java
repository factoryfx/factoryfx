package de.factoryfx.adminui.angularjs.integration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.ByteStreams;
import de.factoryfx.adminui.InMemoryFactoryStorage;
import de.factoryfx.adminui.SinglePrecessInstanceUtil;
import de.factoryfx.adminui.WebAppViewer;
import de.factoryfx.adminui.angularjs.factory.WebGuiApplicationCreator;
import de.factoryfx.adminui.angularjs.factory.WebGuiLayoutFactory;
import de.factoryfx.adminui.angularjs.factory.WebGuiResourceFactory;
import de.factoryfx.adminui.angularjs.factory.WebGuiServerFactory;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.adminui.angularjs.integration.example.ExampleVisitor;
import de.factoryfx.adminui.angularjs.integration.example.ViewCreator;
import de.factoryfx.adminui.angularjs.integration.example.VisitorToTables;
import de.factoryfx.adminui.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.adminui.angularjs.server.resourcehandler.FilesystemFileContentProvider;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.NoUserManagement;
import de.factoryfx.user.UserManagement;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class WebGuiTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        new WebAppViewer(primaryStage, () -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.stringAttribute.set("balblub");
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("BBBBBBBBBBBBBBBB");
            exampleFactoryA.referenceAttribute.set(exampleFactoryB);
            exampleFactoryA.valueListAttribute.add("a111111");
            exampleFactoryA.valueListAttribute.add("b222222");

            exampleFactoryA.mapAttribute.get().put("key1","value1");
            exampleFactoryA.mapAttribute.get().put("key2","value2");
            exampleFactoryA.mapAttribute.get().put("key3","value3");

            exampleFactoryA.byteArrayAttribute.set(new byte[]{1,2,3,4,5});

            for (int i=0; i<3;i++){
                ExampleFactoryB value = new ExampleFactoryB();
                value.stringAttribute.set("i"+i);
                exampleFactoryA.referenceListAttribute.add(value);
            }
            exampleFactoryA.referenceListAttribute.add(exampleFactoryA.referenceAttribute.get());

            DefaultApplicationServer<ExampleVisitor, ExampleFactoryA> exampleApplicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
            exampleApplicationServer.start();

            WebGuiApplicationCreator<ExampleVisitor, ExampleFactoryA> webGuiApplicationCreator=new WebGuiApplicationCreator<>(
                    exampleApplicationServer,
                    Arrays.asList(ExampleFactoryA.class, ExampleFactoryB.class),
                    getUserManagement(),
                    ()->new ExampleVisitor(),
                    new VisitorToTables(),
                    new ViewCreator().create());
            WebGuiServerFactory<ExampleVisitor> defaultFactory = webGuiApplicationCreator.createDefaultFactory();
            if (WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoLarge.png")!=null){
                try(InputStream inputStream= WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoLarge.png")){
                    defaultFactory.webGuiResource.get().layout.get().logoLarge.set(ByteStreams.toByteArray(inputStream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoSmall.png")!=null) {
                try (InputStream inputStream = WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoSmall.png")) {
                    defaultFactory.webGuiResource.get().layout.get().logoSmall.set(ByteStreams.toByteArray(inputStream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            defaultFactory.resourceHandler.set(new ConfigurableResourceHandler(new FilesystemFileContentProvider(Paths.get("./src/main/resources/webapp"),"body {background-color: inherited;}".getBytes(StandardCharsets.UTF_8)), () -> UUID.randomUUID().toString()));
            ApplicationServer<ExampleVisitor, WebGuiServerFactory<ExampleVisitor>> exampleServer = webGuiApplicationCreator.createApplication(defaultFactory, new InMemoryFactoryStorage<>(defaultFactory));
            exampleServer.start();


            WebGuiApplicationCreator<ExampleVisitor, WebGuiServerFactory<ExampleVisitor>> selfServerCreator=new WebGuiApplicationCreator<>(
                    exampleServer,
                    Arrays.asList(WebGuiServerFactory.class, WebGuiLayoutFactory.class, WebGuiResourceFactory.class),
                    getUserManagement(),
                    null,null, Collections.emptyList());
            WebGuiServerFactory<ExampleVisitor> selfDefaultFactory = selfServerCreator.createDefaultFactory();
            selfDefaultFactory.port.set(8087);
            ApplicationServer<ExampleVisitor, WebGuiServerFactory<ExampleVisitor>> selfServer = selfServerCreator.createApplication(selfDefaultFactory, new InMemoryFactoryStorage<>(selfDefaultFactory));
            selfServer.start();


        },"http://localhost:8089/#/login","http://localhost:8087/#/login");
    }

    protected UserManagement getUserManagement() {
        return new NoUserManagement();
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch(args);
    }

}