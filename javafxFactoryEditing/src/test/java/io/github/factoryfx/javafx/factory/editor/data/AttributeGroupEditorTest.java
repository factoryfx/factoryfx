package io.github.factoryfx.javafx.factory.editor.data;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.factory.css.CssUtil;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class AttributeGroupEditorTest  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);



        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1=exampleData1.internal().addBackReferences();
        exampleData1.stringAttribute.set("abc");


        UniformDesign uniformDesign = UniformDesignBuilder.build();
        List<? extends ImmutableValueAttribute<?, ?>> list = Arrays.asList(exampleData1.stringLongAttribute, exampleData1.doubleAttribute);
        AttributeGroupEditor attributeGroupEditor = new AttributeGroupEditor(list, new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)),uniformDesign);
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