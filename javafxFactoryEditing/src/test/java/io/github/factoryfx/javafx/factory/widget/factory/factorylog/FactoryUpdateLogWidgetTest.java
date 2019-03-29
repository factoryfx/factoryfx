package io.github.factoryfx.javafx.factory.widget.factory.factorylog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.javafx.factory.css.CssUtil;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import io.github.factoryfx.factory.log.FactoryLogEntry;
import io.github.factoryfx.factory.log.FactoryLogEntryTreeItem;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.mockito.Mockito;

public class FactoryUpdateLogWidgetTest extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {

        FactoryUpdateLogWidget factoryUpdateLogWidget = new FactoryUpdateLogWidget(new UniformDesignFactory().internal().instance());
        final FactoryLogEntry factoryLogEntry = new FactoryLogEntry(ExampleFactoryA.class, "FactoryX", 0);
        factoryLogEntry.logCreate(21323);
        factoryLogEntry.logStart(5646546);
        final FactoryLogEntry child = new FactoryLogEntry(ExampleFactoryA.class, "FactoryY", 1);
        child.logCreate(21323);
        child.logStart(5646546);


        FactoryLogEntryTreeItem root = new FactoryLogEntryTreeItem(factoryLogEntry, List.of(new FactoryLogEntryTreeItem(child,new ArrayList<>()),new FactoryLogEntryTreeItem(child,new ArrayList<>())));

        final HashSet<FactoryLogEntry> removed = new HashSet<>();
        removed.add(factoryLogEntry);
        removed.add(child);

        FactoryUpdateLog<ExampleFactoryA> factoryLog= new FactoryUpdateLog<>(root, removed, Mockito.mock(MergeDiffInfo.class),56575,"");
        factoryUpdateLogWidget.updateLog(factoryLog);

        BorderPane rootPane = new BorderPane();
        CssUtil.addToNode(rootPane);
        rootPane.setCenter(factoryUpdateLogWidget.createContent());
        primaryStage.setScene(new Scene(rootPane,1200,800));

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}