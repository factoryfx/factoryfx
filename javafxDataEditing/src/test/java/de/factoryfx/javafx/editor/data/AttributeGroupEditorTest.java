package de.factoryfx.javafx.editor.data;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AttributeGroupEditorTest  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());



        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1=exampleData1.internal().prepareUsableCopy();
        exampleData1.stringAttribute.set("abc");


        UniformDesign uniformDesign = UniformDesignBuilder.build();
        List<? extends ImmutableValueAttribute<?, ?>> list = Arrays.asList(exampleData1.stringLongAttribute, exampleData1.doubleAttribute);
        AttributeGroupEditor attributeGroupEditor = new AttributeGroupEditor(list, new AttributeEditorBuilder(AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)),uniformDesign);
        root.setCenter(attributeGroupEditor.createContent());

        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        final ExampleData1 finalexampleData1 = exampleData1;
        syso.setOnAction(event -> System.out.println(ObjectMapperBuilder.build().writeValueAsString(finalexampleData1)));
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