package io.github.factoryfx.javafx.widget.factorydiff;

import java.util.ArrayList;

import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.editor.data.ExampleData1;
import io.github.factoryfx.javafx.editor.data.ExampleData2;
import io.github.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FactoryDiffWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 root = new ExampleData1();
        root.stringAttribute.set("abc");
        root = root.internal().finalise();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()), uniformDesign);
        dataEditor.edit(root);


        root.referenceListAttribute.add(new ExampleData2());
        root.referenceListAttribute.add(new ExampleData2());

        final ExampleData1 newData = root.internal().copy();
        newData.stringAttribute.set("4545544554545");
        newData.referenceListAttribute.add(new ExampleData2());
        DataMerger<ExampleData1> dataMerger = new DataMerger<>(root,root.internal().copy(), newData);

        FactoryDiffWidget<ExampleData1> factoryDiffWidget = new FactoryDiffWidget<>(uniformDesign,new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)));
        factoryDiffWidget.updateMergeDiff(dataMerger.mergeIntoCurrent((p)->true));

        BorderPane rootPane = new BorderPane();
        CssUtil.addToNode(rootPane);
        rootPane.setCenter(factoryDiffWidget.createContent());
        primaryStage.setScene(new Scene(rootPane, 1200, 800));

        primaryStage.show();

    }

    public static void main(String[] args) {
//        System.setProperty("glass.win.minHiDPI","1.5");
//        System.setProperty("glass.win.forceIntegerRenderScale", "false");
//        System.setProperty("glass.win.minHiDPI","1.0");
        Application.launch();
    }

}