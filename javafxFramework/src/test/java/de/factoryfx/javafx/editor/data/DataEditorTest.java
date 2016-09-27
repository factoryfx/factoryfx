package de.factoryfx.javafx.editor.data;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataEditorTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());

        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(new UniformDesign()));
        root.setCenter(dataEditor.createContent());

        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        dataEditor.bind(exampleData1);

        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        syso.setOnAction(event -> {
            System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleData1));
        });
        root.setBottom(syso);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}