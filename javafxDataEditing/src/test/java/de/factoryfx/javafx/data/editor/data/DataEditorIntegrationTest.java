package de.factoryfx.javafx.data.editor.data;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;

public class DataEditorIntegrationTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        CssUtil.addToNode(root);



        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");

        UniformDesign uniformDesign = UniformDesignBuilder.build(Locale.GERMAN);
        DataEditor dataEditor = new DataEditor(new AttributeEditorBuilder(AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign)),uniformDesign, (node, data) -> {
            if (data instanceof ExampleData1) {
                return ((ExampleData1)data).customize(node);
            }
            return node;
        });
        root.setCenter(dataEditor.createContent());

        exampleData1= ObjectMapperBuilder.build().copy(exampleData1);

        exampleData1 = exampleData1.internal().addBackReferences();
        dataEditor.edit(exampleData1);

        primaryStage.setScene(new Scene(root,1200,800));

        Button syso = new Button("syso");
        final ExampleData1 finalexampleData1 = exampleData1;
        syso.setOnAction(event -> System.out.println(ObjectMapperBuilder.build().writeValueAsString(finalexampleData1)));
        Button gc = new Button("gc");
        gc.setOnAction(event -> System.gc());
        HBox buttons = new HBox();
        buttons.getChildren().add(syso);
        buttons.getChildren().add(gc);
        root.setBottom(buttons);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        Application.launch();
    }
}