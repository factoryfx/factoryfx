package de.factoryfx.javafx.widget.datatreeview;

import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.editor.data.ExampleData2;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
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
        exampleData1.internal().prepareUsage();

        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign), uniformDesign);
        dataEditor.edit(exampleData1);

        ObservableList<ExampleData1> dataList = FXCollections.observableArrayList();
        dataList.addAll(exampleData1);
        {
            final ExampleData1 exampleData = new ExampleData1();
            exampleData.referenceListAttribute.add(new ExampleData2());
            exampleData.referenceListAttribute.add(new ExampleData2());
            dataList.addAll(exampleData);
        }
        {
            final ExampleData1 exampleData = new ExampleData1();
            exampleData.referenceListAttribute.add(new ExampleData2());
            exampleData.referenceListAttribute.add(new ExampleData2());
            exampleData.referenceListAttribute.add(new ExampleData2());
            dataList.addAll(exampleData);
        }
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());
        for (int i=0 ;i<100;i++){
            dataList.addAll(new ExampleData1());
        }

        DataTreeViewWidget dataViewWidget = new DataTreeViewWidget(new DataTreeView<>(() -> dataList, new Function<ExampleData1, TreeItem<Data>>() {
            @Override
            public TreeItem<Data> apply(ExampleData1 item) {
                final TreeItem<Data> dataTreeItem = new TreeItem<>(item);
                dataTreeItem.getChildren().addAll(item.referenceListAttribute.get().stream().map((Function<ExampleData2, TreeItem<Data>>) TreeItem::new).collect(Collectors.toList()));
                dataTreeItem.setExpanded(true);
                return dataTreeItem;
            }
        }),dataEditor,uniformDesign);

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
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