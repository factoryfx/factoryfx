package io.github.factoryfx.factory.typescript.generator.testserver;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.typescript.generator.TsGenerator;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class DomTestServerMain {


    public static void main(String[] args) {
        Path targetDirJs = Paths.get("src/main/resources/js/factoryEditing");
        if (!targetDirJs.toFile().exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }

        TsGenerator<TestServerFactory> jsClassCreator=new TsGenerator<>(targetDirJs, Set.of());
        jsClassCreator.clearTargetDir();
        jsClassCreator.generateJs();


        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);


        Microservice<Server, TestServerFactory> microservice = new TestServerBuilder().create().microservice().build();
        microservice.start();

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost:8005/microservice/index.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
