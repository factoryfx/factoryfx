package de.factoryfx.javafx.data.widget.tree;

import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.editor.data.ExampleData1;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataTreeWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {




        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");
        exampleData1 = exampleData1.internal().addBackReferences();

        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeEditorBuilder(AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(UniformDesignBuilder.build())),uniformDesign);
        dataEditor.edit(exampleData1);



        DataTreeWidget dataTreeWidget= new DataTreeWidget(dataEditor,UniformDesignBuilder.build());



        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);
        root.setCenter(dataTreeWidget.createContent());
        primaryStage.setScene(new Scene(root,1200,800));

        primaryStage.show();

        dataTreeWidget.edit(exampleData1);


    }

    public static void main(String[] args) {
        Application.launch();
    }
}