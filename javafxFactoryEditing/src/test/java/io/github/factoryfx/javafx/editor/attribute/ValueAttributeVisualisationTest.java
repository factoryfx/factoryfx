package io.github.factoryfx.javafx.editor.attribute;

import javafx.application.Application;
import javafx.stage.Stage;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.UniformDesignBuilder;

public class ValueAttributeVisualisationTest extends Application {
    StringAttribute attribute = new StringAttribute();

    //not unit test cause build server has no x server
    public void test_weakListener(){

        AttributeVisualisationMappingBuilder attributeEditor = new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(UniformDesignBuilder.build()));
        StringAttribute attribute = new StringAttribute();
        AttributeVisualisation attributeVisualisation = attributeEditor.getAttributeVisualisation(attribute, null, null);
        attributeVisualisation.createVisualisation();

//        System.gc();

        System.out.println("breakpoint and use profiler to check no visualisation in memory ");

    }

    // start with
    // --add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.util=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ValueAttributeVisualisationTest().test_weakListener();
    }
}