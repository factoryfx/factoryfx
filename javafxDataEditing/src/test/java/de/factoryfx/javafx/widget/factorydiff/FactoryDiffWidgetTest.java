package de.factoryfx.javafx.widget.factorydiff;

import java.util.ArrayList;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.editor.data.ExampleData2;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FactoryDiffWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 root = new ExampleData1();
        root.stringAttribute.set("abc");
        root = root.internal().prepareUsableCopy();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeEditorBuilder(new ArrayList<>()), uniformDesign);
        dataEditor.edit(root);


        root.referenceListAttribute.add(new ExampleData2());
        root.referenceListAttribute.add(new ExampleData2());

        final ExampleData1 newData = root.internal().copy();
        newData.stringAttribute.set("4545544554545");
        newData.referenceListAttribute.add(new ExampleData2());
        DataMerger dataMerger = new DataMerger(root,root.internal().copy(), newData);

        FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign,new AttributeEditorBuilder(AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)));
        factoryDiffWidget.updateMergeDiff(dataMerger.mergeIntoCurrent((p)->true));

        BorderPane rootPane = new BorderPane();
        rootPane.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
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