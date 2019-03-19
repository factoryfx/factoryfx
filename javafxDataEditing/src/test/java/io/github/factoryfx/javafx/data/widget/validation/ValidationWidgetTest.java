package io.github.factoryfx.javafx.data.widget.validation;

import java.util.ArrayList;

import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.data.editor.data.ExampleData1;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ValidationWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {




        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().addBackReferences();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);
        dataEditor.edit(exampleData1);



        ValidationWidget validationWidget= new ValidationWidget(exampleData1,dataEditor,UniformDesignBuilder.build());


        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);
        root.setCenter(validationWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();



    }

    public static void main(String[] args) {
        Application.launch();
    }
}