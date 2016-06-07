package de.factoryfx.richclient.framework.view;

import java.util.function.Consumer;

import de.factoryfx.guimodel.RuntimeQueryView;
import de.factoryfx.guimodel.TableColumn;
import de.factoryfx.richclient.framework.widget.Widget;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class RuntimeView implements Widget{
    public final RuntimeQueryView<?> runtimeQueryView;

    public RuntimeView(RuntimeQueryView<?> runtimeQueryView) {
        this.runtimeQueryView = runtimeQueryView;
    }

    @Override
    public Node createContent() {
        BorderPane borderPane = new BorderPane();
        HBox searchPane = new HBox(3);
        searchPane.setAlignment(Pos.CENTER_LEFT);
        searchPane.setPadding(new Insets(3));
        borderPane.setTop(searchPane);

        searchPane.getChildren().add(new Label("Search"));
        TextField searchQuery = new TextField();
        HBox.setHgrow(searchQuery, Priority.ALWAYS);
        searchPane.getChildren().add(searchQuery);


        TableView<Object> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        runtimeQueryView.table.tableColumn.forEach(new Consumer<TableColumn<?>>() {
            @Override
            public void accept(TableColumn<?> tableColumn) {
                javafx.scene.control.TableColumn<Object, String> column = new javafx.scene.control.TableColumn<>(tableColumn.name);
                column.setCellValueFactory(param -> new SimpleStringProperty(tableColumn.apply(param.getValue())));
                tableView.getColumns().add(column);
            }
        });
        borderPane.setCenter(tableView);


        Button searchButton = new Button("search");
        searchButton.setOnAction(event -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(runtimeQueryView.getData(searchQuery.getText()));
        });
        searchPane.getChildren().add(searchButton);


        return borderPane;
    }

}
