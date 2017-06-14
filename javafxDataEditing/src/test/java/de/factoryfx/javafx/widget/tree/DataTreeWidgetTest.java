package de.factoryfx.javafx.widget.tree;

import java.util.ArrayList;

import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataTreeWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {




        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().prepareUsableCopy();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeEditorBuilder(new ArrayList<>()),uniformDesign);
        dataEditor.edit(exampleData1);



        DataTreeWidget dataTreeWidget= new DataTreeWidget(dataEditor,exampleData1,UniformDesignBuilder.build());



        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        root.setCenter(dataTreeWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();



    }

    public static void main(String[] args) {
        Application.launch();
    }
}