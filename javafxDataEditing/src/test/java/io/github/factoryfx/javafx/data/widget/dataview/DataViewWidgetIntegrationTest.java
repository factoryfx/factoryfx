package io.github.factoryfx.javafx.data.widget.dataview;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.data.editor.data.ExampleData1;
import io.github.factoryfx.javafx.data.editor.data.ExampleData2;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataViewWidgetIntegrationTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().addBackReferences();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)), uniformDesign);
        dataEditor.edit(exampleData1);

        ObservableList<Data> dataList = FXCollections.observableArrayList();
        dataList.addAll(exampleData1);
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());

        exampleData1.referenceListAttribute.add(new ExampleData2());
        exampleData1.referenceListAttribute.add(new ExampleData2());

        DataViewWidget<ExampleData2> dataViewWidget = new DataViewWidget<>(dataEditor,uniformDesign);
        dataViewWidget.edit(exampleData1.referenceListAttribute);
        DataViewWidget<Data> dataViewWidget2 = new DataViewWidget<>(dataEditor,uniformDesign);
        dataViewWidget2.edit(dataList);

        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);
        root.setCenter(dataViewWidget.createContent());
        primaryStage.setScene(new Scene(root, 1200, 800));

        primaryStage.show();

    }

    public static void main(String[] args) {
//        System.setProperty("glass.win.minHiDPI","1.5");
//        System.setProperty("glass.win.forceIntegerRenderScale", "false");
//        System.setProperty("glass.win.minHiDPI","1.0");
        Application.launch();
    }
}