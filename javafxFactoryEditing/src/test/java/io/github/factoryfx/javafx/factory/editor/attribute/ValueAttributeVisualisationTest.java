package io.github.factoryfx.javafx.factory.editor.attribute;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import javafx.application.Application;
import javafx.stage.Stage;

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

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ValueAttributeVisualisationTest().test_weakListener();
    }
}