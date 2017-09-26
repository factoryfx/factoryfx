package io.github.factoryfx.vuejs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServerFactory;
import de.factoryfx.testutils.SingleProcessInstanceUtil;
import de.factoryfx.testutils.WebAppViewer;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.nop.NoUserManagement;
import de.factoryfx.user.nop.NoUserManagementFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class WebGuiTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        new WebAppViewer(primaryStage, () -> {

            FactoryTreeBuilder<Void,VuejsTestServer,VuejsTestServerFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(VuejsTestServerFactory.class);
            factoryTreeBuilder.addFactory(VuejsTestServerFactory.class, Scope.SINGLETON);
            factoryTreeBuilder.addFactory(JettyServerFactory.class, Scope.SINGLETON, context -> {
                JettyServerFactory<Void> serverFactory = new JettyServerFactory<>();
                serverFactory.resources.add(new ProjectFileStructureServingResourceFactory());
                HttpServerConnectorFactory<Void> connectorFactory = new HttpServerConnectorFactory<>();
                connectorFactory.host.set("localhost");
                connectorFactory.port.set(8087);
                serverFactory.connectors.add(connectorFactory);

                ApplicationServerResourceFactory<Void, Object, FactoryBase<Object, Void>> applicationServerResourceFactory = new ApplicationServerResourceFactory<>();
                applicationServerResourceFactory.userManagement.set(new NoUserManagementFactory());
                serverFactory.resources.add(applicationServerResourceFactory);

                return serverFactory;
            });




            ApplicationServer<Void, VuejsTestServer, VuejsTestServerFactory> exampleApplicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), new InMemoryFactoryStorage<>(factoryTreeBuilder.buildTree()));
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

        },"http://localhost:8087");
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