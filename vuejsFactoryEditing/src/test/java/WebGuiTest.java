import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.testutils.SingleProcessInstanceUtil;
import de.factoryfx.testutils.WebAppViewer;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.nop.NoUserManagement;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class WebGuiTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        new WebAppViewer(primaryStage, () -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.stringAttribute.set("balblub");
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("BBBBBBBBBBBBBBBB");
            exampleFactoryA.referenceAttribute.set(exampleFactoryB);


            ApplicationServer<Void, ExampleLiveObjectA, ExampleFactoryA> exampleApplicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), new InMemoryFactoryStorage<>(exampleFactoryA));
            exampleApplicationServer.start();

            {
//                WebGuiApplicationCreator<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA> webGuiApplicationCreator = new WebGuiApplicationCreator<>(exampleApplicationServer, Arrays.asList(ExampleFactoryA.class, ExampleFactoryB.class), getUserManagement(), () -> new ExampleVisitor(), new VisitorToTables(), new ViewCreator().create());
//                HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA> defaultFactory = webGuiApplicationCreator.createDefaultFactory();
//                if (WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoLarge.png") != null) {
//                    try (InputStream inputStream = WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoLarge.png")) {
//                        defaultFactory.webGuiResource.get().layout.get().logoLarge.set(ByteStreams.toByteArray(inputStream));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                if (WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoSmall.png") != null) {
//                    try (InputStream inputStream = WebGuiApplicationCreator.class.getResourceAsStream("/logo/logoSmall.png")) {
//                        defaultFactory.webGuiResource.get().layout.get().logoSmall.set(ByteStreams.toByteArray(inputStream));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                defaultFactory.resourceHandler.set(new ConfigurableResourceHandler(new FilesystemFileContentProvider(Paths.get("./src/main/resources/webapp"), "body {background-color: inherited;}".getBytes(StandardCharsets.UTF_8)), () -> UUID.randomUUID().toString()));
//                FactoryStorage<Void,HttpServer,HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA>> lvtInMemoryFactoryStorage = new InMemoryFactoryStorage<>(defaultFactory);
//                ApplicationServer<Void,HttpServer,HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA>> exampleServer = webGuiApplicationCreator.createApplication(lvtInMemoryFactoryStorage);
//                exampleServer.start();
//
//                {
//                    WebGuiApplicationCreator<Void,HttpServer,HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA>> selfServerCreator = new WebGuiApplicationCreator<>(exampleServer, new ClasspathBasedFactoryProvider().get(SessionStorageFactory.class), new NoUserManagement(), null, null, Collections.emptyList());
//                    HttpServerFactory<Void,HttpServer,HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA>> selfDefaultFactory = selfServerCreator.createDefaultFactory();
//                    selfDefaultFactory.port.set(8087);
//                    ApplicationServer<Void, HttpServer, HttpServerFactory<Void, HttpServer, HttpServerFactory<ExampleVisitor, ExampleLiveObjectA, ExampleFactoryA>>> selfServer = selfServerCreator.createApplication(new InMemoryFactoryStorage<>(selfDefaultFactory));
//                    selfServer.start();
//                }

            }

        },"http://localhost:8087/#/login","http://localhost:8089/#/login");
    }

    protected UserManagement getUserManagement() {
        return new NoUserManagement();
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SingleProcessInstanceUtil.enforceSingleProcessInstance(37453);
        Application.launch(args);
    }

}