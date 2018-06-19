package de.factoryfx.javafx.data.widget.datatreeview;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.editor.data.ExampleData1;
import de.factoryfx.javafx.data.editor.data.ExampleData2;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataTreeViewWidgetTest extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeEditorBuilder(new ArrayList<>()), uniformDesign);
        dataEditor.edit(exampleData1);

        for (int i=0 ;i<100;i++){
            exampleData1.referenceListAttribute.add(new ExampleData2());
        }

        ObservableList<ExampleData1> dataList = FXCollections.observableArrayList();
        dataList.add(exampleData1);
//        {
//            final ExampleData1 exampleData = new ExampleData1();
//            exampleData.referenceListAttribute.add(new ExampleData2());
//            exampleData.referenceListAttribute.add(new ExampleData2());
//            dataList.addAll(exampleData);
//        }
//        {
//            final ExampleData1 exampleData = new ExampleData1();
//            exampleData.referenceListAttribute.add(new ExampleData2());
//            exampleData.referenceListAttribute.add(new ExampleData2());
//            exampleData.referenceListAttribute.add(new ExampleData2());
//            dataList.addAll(exampleData);
//        }
//        dataList.addAll(new ExampleData1());
//        dataList.addAll(new ExampleData1());
//        for (int i=0 ;i<100;i++){
//            exampleData1.referenceListAttribute.add(new ExampleData2());
//        }

        exampleData1 = exampleData1.internal().addBackReferences();

        DataTreeViewWidget<ExampleData1> dataViewWidget = new DataTreeViewWidget<>(new DataTreeView<>(() -> dataList, item -> {
            final TreeItem<Data> dataTreeItem = new TreeItem<>(item);
            dataTreeItem.getChildren().addAll(item.referenceListAttribute.stream().map((Function<ExampleData2, TreeItem<Data>>) value -> new TreeItem<>(value)).collect(Collectors.toList()));
            dataTreeItem.setExpanded(true);
            return dataTreeItem;
        }),dataEditor);

        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);
        root.setCenter(dataViewWidget.createContent());
        primaryStage.setScene(new Scene(root, 1200, 800));

        final Button clear = new Button("clear");
        clear.setOnAction(event -> root.setCenter(null));
        root.setBottom(clear);


        primaryStage.show();

    }

    public static void main(String[] args) {
//        System.setProperty("glass.win.minHiDPI","1.5");
//        System.setProperty("glass.win.forceIntegerRenderScale", "false");
//        System.setProperty("glass.win.minHiDPI","1.0");
        Application.launch();
    }
}