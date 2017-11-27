package de.factoryfx.javafx.widget.factorylog;

import java.util.ArrayList;
import java.util.HashSet;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.mockito.Mockito;

public class FactoryUpdateLogWidgetTest extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {

        FactoryUpdateLogWidget factoryUpdateLogWidget = new FactoryUpdateLogWidget(new UniformDesignFactory<>().internalFactory().instance());
        final FactoryLogEntry factoryLogEntry = new FactoryLogEntry(ExampleFactoryA.class, "FactoryX",new ArrayList<>(),new ArrayList<>(), 0);
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,21323));
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,5646546));
        final FactoryLogEntry child = new FactoryLogEntry(ExampleFactoryA.class, "FactoryY",new ArrayList<>(),new ArrayList<>(), 1);
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,3434343));
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,987768878));
        factoryLogEntry.children.add(child);
        factoryLogEntry.children.add(child);

        final HashSet<FactoryLogEntry> removed = new HashSet<>();
        removed.add(factoryLogEntry);
        removed.add(child);

        FactoryUpdateLog<Data> factoryLog= new FactoryUpdateLog<>(factoryLogEntry, removed, Mockito.mock(MergeDiffInfo.class),56575);
        factoryUpdateLogWidget.updateLog(factoryLog);

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        root.setCenter(factoryUpdateLogWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}