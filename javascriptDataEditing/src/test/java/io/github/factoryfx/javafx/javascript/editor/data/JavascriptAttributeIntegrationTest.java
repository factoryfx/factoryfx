package io.github.factoryfx.javafx.javascript.editor.data;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.javascript.editor.attribute.visualisation.JavascriptAttributeVisualisation;
import io.github.factoryfx.javascript.data.attributes.types.JavascriptAttribute;
import io.github.factoryfx.javafx.editor.attribute.builder.AttributeVisualisationBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JavascriptAttributeIntegrationTest extends Application{

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);



        ExampleJavascript exampleJavascript = new ExampleJavascript();



        UniformDesign uniformDesign = new UniformDesign(Locale.ENGLISH, Color.web("#FF7979"),Color.web("#F0AD4E"),Color.web("#5BC0DE"),Color.web("#5CB85C"),Color.web("#5494CB"),Color.web("#B5B5B5"),false);

        List<AttributeVisualisationBuilder> singleAttributeVisualisationBuilders = AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign);
        AttributeVisualisationBuilder editorBuilder = new AttributeVisualisationBuilder() {
            @Override
            public boolean isListItemEditorFor(Attribute<?, ?> attribute) {
                return false;
            }

            @Override
            public boolean isEditorFor(Attribute<?, ?> attribute) {
                return JavascriptAttribute.class == attribute.getClass();
            }

            @Override
            public AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<FactoryBase<?,?>> navgigateTo, FactoryBase<?,?> previousData) {
                return new JavascriptAttributeVisualisation((JavascriptAttribute<?>) attribute, new ValidationDecoration(uniformDesign));
            }

            @Override
            public AttributeVisualisation createValueListVisualisation(Attribute<?, ?> attribute) {
                return null;
            }
        };
        singleAttributeVisualisationBuilders.add(editorBuilder);
        AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder = new AttributeVisualisationMappingBuilder(singleAttributeVisualisationBuilders);


        DataEditor dataEditor = new DataEditor(attributeVisualisationMappingBuilder,uniformDesign);
        root.setCenter(dataEditor.createContent());

        exampleJavascript= ObjectMapperBuilder.build().copy(exampleJavascript);

        exampleJavascript = exampleJavascript.internal().finalise();
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