package de.factoryfx.javafx.widget.factorylog;

import java.util.ArrayList;
import java.util.HashSet;

import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.mockito.Mockito;

public class FactoryLogWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FactoryUpdateLogWidget factoryUpdateLogWidget = new FactoryUpdateLogWidget(new UniformDesignFactory<>().internalFactory().instance());
        final FactoryLogEntry factoryLogEntry = new FactoryLogEntry("FactoryX",new ArrayList<>(),new ArrayList<>());
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,21323));
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,5646546));
        final FactoryLogEntry child = new FactoryLogEntry("FactoryY",new ArrayList<>(),new ArrayList<>());
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,3434343));
        child.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,987768878));
        factoryLogEntry.children.add(child);

        final HashSet<FactoryLogEntry> removed = new HashSet<>();
        removed.add(factoryLogEntry);
        removed.add(child);

        FactoryUpdateLog factoryLog= new FactoryUpdateLog(factoryLogEntry, removed, new MergeDiffInfo(Mockito.mock(MergeDiff.class)),56575);
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