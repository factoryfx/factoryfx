package de.factoryfx.development.angularjs.integration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.ByteStreams;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.development.WebAppViewer;
import de.factoryfx.development.angularjs.server.WebGuiResource;
import de.factoryfx.development.angularjs.server.WebGuiServer;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.guimodel.GuiModel;
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


            for (int i=0; i<3;i++){
                ExampleFactoryB value = new ExampleFactoryB();
                value.stringAttribute.set("i"+i);
                exampleFactoryA.referenceListAttribute.add(value);
            }

            GuiModel guiModel = new GuiModel();
            guiModel.title.en("Test example");
            try(InputStream inputStream= WebGuiTest.class.getResourceAsStream("/logo/logoLarge.png")){
                guiModel.logoLarge= ByteStreams.toByteArray(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try(InputStream inputStream= WebGuiTest.class.getResourceAsStream("/logo/logoSmall.png")){
                guiModel.logoSmall= ByteStreams.toByteArray(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            DefaultApplicationServer<Void, ExampleFactoryA> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(exampleFactoryA));
            applicationServer.start();
            new WebGuiServer(8089, "localhost", new WebGuiResource<>(guiModel,applicationServer, () -> Arrays.asList(ExampleFactoryA.class,ExampleFactoryB.class),Arrays.asList(Locale.ENGLISH,Locale.GERMAN))).start();
        },"http://localhost:8089/#/login");
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch(args);
    }

}