package de.factoryfx.javafx.javascript.editor.data;

import java.util.Optional;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.javascript.editor.attribute.visualisation.JavascriptAttributeVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javascript.data.attributes.types.Javascript;
import de.factoryfx.javascript.data.attributes.types.JavascriptAttribute;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class JavascriptAttributeIntegrationTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());



        ExampleJavascript exampleJavascript = new ExampleJavascript();

        UniformDesign uniformDesign = new UniformDesignFactory<>().internalFactory().instance();
        AttributeEditorFactory attributeEditorFactory = new AttributeEditorFactory(uniformDesign);
        attributeEditorFactory.addEditorAssociation(a->{
            if (Javascript.class==a.internal_getAttributeType().dataType){
                JavascriptAttribute javascriptAttribute = (JavascriptAttribute) a;
                return Optional.of(new AttributeEditor<>(javascriptAttribute,new JavascriptAttributeVisualisation(javascriptAttribute),uniformDesign));
            }

            return Optional.empty();
        });


        DataEditor dataEditor = new DataEditor(attributeEditorFactory,uniformDesign);
        root.setCenter(dataEditor.createContent());

        exampleJavascript= ObjectMapperBuilder.build().copy(exampleJavascript);

        exampleJavascript = exampleJavascript.internal().prepareUsableCopy();
        dataEditor.edit(exampleJavascript);

        primaryStage.setScene(new Scene(root,1200,800));

        Button gc = new Button("gc");
        gc.setOnAction(event -> System.gc());

        Button exec = new Button("execute");
        ExampleJavascript pExampleJavascript = exampleJavascript;
        exec.setOnAction(event -> pExampleJavascript.specialAttribute.get().execute(System.out));

        HBox buttons = new HBox();
        buttons.getChildren().add(gc);
        buttons.getChildren().add(exec);
        root.setBottom(buttons);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        Application.launch();
    }
}