package de.factoryfx.javafx.widget.tree;

import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataTreeWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {




        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1.internal().prepareUsage();

        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign),uniformDesign);
        dataEditor.edit(exampleData1);



        DataTreeWidget dataTreeWidget= new DataTreeWidget(dataEditor,exampleData1,new UniformDesignFactory<>().instance());



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