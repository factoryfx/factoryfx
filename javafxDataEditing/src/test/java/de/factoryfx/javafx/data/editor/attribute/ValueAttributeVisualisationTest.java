package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.stage.Stage;

import static org.junit.Assert.*;

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