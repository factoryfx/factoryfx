package de.factoryfx.javafx.widget.masterdetail;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MasterDetailWidgetTest  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());


        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");

        UniformDesign uniformDesign = new UniformDesign();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign,exampleData1),uniformDesign);
        dataEditor.edit(exampleData1);

        ObservableList<Data> dataList = FXCollections.observableArrayList();
        for (int i=0;i<100;i++){
            ExampleData1 data = new ExampleData1();
            data.stringAttribute.set(""+i);
            dataList.add(data);
        }
        MasterDetailWidget masterDetailWidget = new MasterDetailWidget(new DataView(dataList), dataEditor);
        root.setCenter(masterDetailWidget.createContent());

        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        syso.setOnAction(event -> {
            System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleData1));
        });
        Button gc = new Button("gc");
        gc.setOnAction(event -> {
            System.gc();
        });
        HBox buttons = new HBox();
        buttons.getChildren().add(syso);
        buttons.getChildren().add(gc);
        root.setBottom(buttons);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}