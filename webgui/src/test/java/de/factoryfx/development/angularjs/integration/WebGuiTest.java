package de.factoryfx.development.angularjs.integration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.ByteStreams;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.development.WebAppViewer;
import de.factoryfx.development.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.development.angularjs.server.resourcehandler.FilesystemFileContentProvider;
import de.factoryfx.development.factory.WebGuiApplication;
import de.factoryfx.development.factory.WebGuiServerFactory;
import de.factoryfx.factory.FactoryManager;
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
            exampleFactoryA.valueListAttribute.add("111111");
            exampleFactoryA.valueListAttribute.add("222222");

            exampleFactoryA.mapAttribute.get().put("key1","value1");
            exampleFactoryA.mapAttribute.get().put("key2","value2");
            exampleFactoryA.mapAttribute.get().put("key3","value3");

            for (int i=0; i<3;i++){
                ExampleFactoryB value = new ExampleFactoryB();
                value.stringAttribute.set("i"+i);
                exampleFactoryA.referenceListAttribute.add(value);
            }
            DefaultApplicationServer<Void, ExampleFactoryA> exampleApplicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
            exampleApplicationServer.start();

            WebGuiApplication<Void, ExampleFactoryA> webGuiApplication =new WebGuiApplication<>(exampleApplicationServer,Arrays.asList(ExampleFactoryA.class, ExampleFactoryB.class),(WebGuiServerFactory root)->new InMemoryFactoryStorage<>(root),getUserManagement(),
                    (config)->{
                        if (WebGuiApplication.class.getResourceAsStream("/logo/logoLarge.png")!=null){
                            try(InputStream inputStream= WebGuiApplication.class.getResourceAsStream("/logo/logoLarge.png")){
                                config.webGuiResource.get().layout.get().logoLarge.setBytes(ByteStreams.toByteArray(inputStream));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (WebGuiApplication.class.getResourceAsStream("/logo/logoSmall.png")!=null) {
                            try (InputStream inputStream = WebGuiApplication.class.getResourceAsStream("/logo/logoSmall.png")) {
                                config.webGuiResource.get().layout.get().logoSmall.setBytes(ByteStreams.toByteArray(inputStream));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        config.resourceHandler.set(new ConfigurableResourceHandler(new FilesystemFileContentProvider(Paths.get("./src/main/resources/webapp")), () -> UUID.randomUUID().toString()));
                    });
            webGuiApplication.start();

//            WebGuiApplication<WebGuiServer, WebGuiServerFactory> webGuiApplicationSelf =new WebGuiApplication<>(webGuiApplication.getServer(),8088,Arrays.asList(WebGuiServerFactory.class, WebGuiLayoutFactory.class, WebGuiResourceFactory.class),(root)->new InMemoryFactoryStorage<>(root),getUserManagement());
//            webGuiApplicationSelf.start();
        },"http://localhost:8089/#/login");
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