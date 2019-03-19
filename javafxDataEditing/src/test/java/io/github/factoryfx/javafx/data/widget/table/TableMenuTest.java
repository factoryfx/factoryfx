package io.github.factoryfx.javafx.data.widget.table;

import io.github.factoryfx.javafx.UniformDesignBuilder;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class TableMenuTest extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TableMenu<ExampleItem> tableMenu = new TableMenu<>(UniformDesignBuilder.build());
        TreeTableView<ExampleItem> treeTableView = new TreeTableView<>();
        {
            TreeTableColumn<ExampleItem, String> column = new TreeTableColumn<>();
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().text1));
            treeTableView.getColumns().add(column);
        }
        {
            TreeTableColumn<ExampleItem, String> column = new TreeTableColumn<>();
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().text2));
            treeTableView.getColumns().add(column);
        }
        tableMenu.addMenu(treeTableView);

        TreeItem<ExampleItem> root = new TreeItem<>(new ExampleItem("root", ""));
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        root.getChildren().add(new TreeItem<>(new ExampleItem("1t1","1t2")));
        root.getChildren().add(new TreeItem<>(new ExampleItem("2t1","2t2")));
        root.getChildren().add(new TreeItem<>(new ExampleItem("3t1","3t2")));

        primaryStage.setScene(new Scene(treeTableView,1200,800));
        primaryStage.show();
    }

    private static class ExampleItem{
        private final String text1;
        private final String text2;

        private ExampleItem(String text1, String text2) {
            this.text1 = text1;
            this.text2 = text2;
        }
    }
}