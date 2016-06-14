package de.factoryfx.development.angularjs.server;

import java.util.Arrays;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.development.WebAppViewer;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.DefaultApplicationServer;
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


            for (int i=0; i<5;i++){
                ExampleFactoryB value = new ExampleFactoryB();
                value.stringAttribute.set("i");
                exampleFactoryA.referenceListAttribute.add(value);
            }

            DefaultApplicationServer<Void, ExampleFactoryA> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
            applicationServer.start();
            new WebGuiServer(8089, "localhost", new WebGuiResource(applicationServer, () -> Arrays.asList(ExampleFactoryA.class,ExampleFactoryB.class))).start();
        },"http://localhost:8089/#/view1");
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch(args);
    }

}