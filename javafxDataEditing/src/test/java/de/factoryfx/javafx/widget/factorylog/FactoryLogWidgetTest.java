package de.factoryfx.javafx.widget.factorylog;

import java.util.ArrayList;

import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FactoryLogWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FactoryLogWidget factoryLogWidget= new FactoryLogWidget(new UniformDesignFactory<>().internalFactory().instance());
        final FactoryLogEntry factoryLogEntry = new FactoryLogEntry("FactoryX",new ArrayList<>(),new ArrayList<>());
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,21323));
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,5646546));
        final FactoryLogEntry child = new FactoryLogEntry("FactoryY",new ArrayList<>(),new ArrayList<>());
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,21323));
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,5646546));
        factoryLogEntry.children.add(child);

        factoryLogWidget.updateLog(factoryLogEntry);

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        root.setCenter(factoryLogWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}