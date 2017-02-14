package de.factoryfx.javafx.widget.validation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ValidationWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {




        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().prepareUsableCopy();

        UniformDesign uniformDesign = new UniformDesignFactory<>().internalFactory().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign),uniformDesign);
        dataEditor.edit(exampleData1);



        ValidationWidget validationWidget= new ValidationWidget(exampleData1,dataEditor,new UniformDesignFactory<>().internalFactory().instance());


        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        root.setCenter(validationWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();



    }

    public static void main(String[] args) {
        Application.launch();
    }
}