package de.factoryfx.javafx.widget.factorylog;

import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FactoryLogWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().prepareUsableCopy();

        UniformDesign uniformDesign = new UniformDesignFactory<>().internalFactory().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign),uniformDesign);
        dataEditor.edit(exampleData1);



        FactoryLogWidget factoryLogWidget= new FactoryLogWidget(new UniformDesignFactory<>().internalFactory().instance());
        final FactoryLogEntry factoryLogEntry = new FactoryLogEntry(new ExampleFactoryA());
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,21323));
        factoryLogEntry.events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,5646546));
        final FactoryLogEntry child = new FactoryLogEntry(new ExampleFactoryA());
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