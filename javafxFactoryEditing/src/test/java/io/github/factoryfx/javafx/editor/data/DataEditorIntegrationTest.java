package io.github.factoryfx.javafx.editor.data;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.view.View;
import io.github.factoryfx.javafx.view.ViewDescription;
import io.github.factoryfx.javafx.view.container.ViewsDisplayWidget;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;

public class DataEditorIntegrationTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build(Locale.GERMAN);


        ViewsDisplayWidget viewsDisplayWidget = new ViewsDisplayWidget(new TabPane(), uniformDesign);
        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);

        root.setCenter(viewsDisplayWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        Button gc = new Button("gc");
        gc.setOnAction(event -> System.gc());
        HBox buttons = new HBox();
        buttons.getChildren().add(syso);
        buttons.getChildren().add(gc);
        Button openView = new Button("open view");
        buttons.getChildren().add(openView);
        Runnable openViewRunnable= ()->{
            final DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)),uniformDesign, (node, data) -> {
                if (data instanceof ExampleData1) {
                    return ((ExampleData1)data).customize(node);
                }
                return node;
            });
            ExampleData1 exampleData1 = new ExampleData1();
            exampleData1.stringAttribute.set("abc");
            exampleData1.valueListAttribute.set(List.of("a", "b"));
            exampleData1 = exampleData1.internal().finalise();
            dataEditor.edit(exampleData1);
            syso.setOnAction(event -> System.out.println(ObjectMapperBuilder.build().writeValueAsString(dataEditor)));
            viewsDisplayWidget.show(new View(new ViewDescription(new LanguageText("Dataeditor"),null,uniformDesign),viewsDisplayWidget,dataEditor));
        };
        openView.setOnAction(event -> openViewRunnable.run());
        openViewRunnable.run();

        Button listThreads = new Button("list threads");
        listThreads.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                System.out.println(threadSet.size());
            }
        });
        buttons.getChildren().add(listThreads);

        root.setBottom(buttons);
        primaryStage.show();




    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        Application.launch();
    }
}