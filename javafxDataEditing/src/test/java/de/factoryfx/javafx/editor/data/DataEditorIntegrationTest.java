package de.factoryfx.javafx.editor.data;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DataEditorIntegrationTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());



        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");

        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign),uniformDesign);
        root.setCenter(dataEditor.createContent());

        exampleData1.internal().prepareUsage();
        dataEditor.edit(exampleData1);

        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        syso.setOnAction(event -> System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleData1)));
        Button gc = new Button("gc");
        gc.setOnAction(event -> System.gc());
        HBox buttons = new HBox();
        buttons.getChildren().add(syso);
        buttons.getChildren().add(gc);
        root.setBottom(buttons);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        Application.launch();
    }
}