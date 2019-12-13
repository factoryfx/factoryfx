package io.github.factoryfx.javafx.widget.dataview;


import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.editor.data.ExampleData1;
import io.github.factoryfx.javafx.editor.data.ExampleData2;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidget;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataViewWidgetIntegrationTest extends Application {
// --add-exports=javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.input=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.font=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.glass.utils=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.prism=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.css=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.util=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.collections=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.logging=ALL-UNNAMED

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().finalise();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)), uniformDesign);
        dataEditor.edit(exampleData1);

        ObservableList<ExampleData1> dataList = FXCollections.observableArrayList();
        dataList.addAll(exampleData1);
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());

        exampleData1.referenceListAttribute.add(new ExampleData2());
        exampleData1.referenceListAttribute.add(new ExampleData2());

        DataViewWidget<ExampleData1,Void,ExampleData2> dataViewWidget = new DataViewWidget<>(dataEditor,uniformDesign);
        dataViewWidget.edit(exampleData1.referenceListAttribute);
        DataViewWidget<ExampleData1,Void,ExampleData1> dataViewWidget2 = new DataViewWidget<>(dataEditor,uniformDesign);
        dataViewWidget2.edit(dataList);

        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);
        root.setCenter(dataViewWidget.createContent());
        primaryStage.setScene(new Scene(root, 1200, 800));

        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch();
    }
}