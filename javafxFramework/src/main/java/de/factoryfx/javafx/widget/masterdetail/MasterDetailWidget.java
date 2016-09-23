package de.factoryfx.javafx.widget.masterdetail;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import de.factoryfx.javafx.widget.select.SelectDataListWidget;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MasterDetailWidget<T extends Data> implements CloseAwareWidget {

    private final SelectDataListWidget<T> selectDataListWidget;
    private final DataEditor dataEditor;//prevent garbage collection for bind to select
    private final ChangeListener<T> listener;
    private double dividerPosition = 0.333;

    public MasterDetailWidget(SelectDataListWidget<T> selectDataListWidget, DataEditor dataEditor) {
        this.selectDataListWidget = selectDataListWidget;
        this.dataEditor = dataEditor;

        listener = (observable, oldValue, newValue) -> {
            dataEditor.bind(newValue);
        };
        selectDataListWidget.selected.addListener(listener);
    }

    @Override
    public void closeNotifier() {
        listener.changed(null, null, null);
    }

    @Override
    public Node createContent() {
//        MasterDetailPane pane = new MasterDetailPane();
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        VBox vbox = new VBox(0);
        Node selectListWidgetContent = selectDataListWidget.createContent();
        VBox.setVgrow(selectListWidgetContent, Priority.ALWAYS);
        vbox.getChildren().add(selectListWidgetContent);

        BorderPane borderPaneWrapper = new BorderPane();
        borderPaneWrapper.setCenter(vbox);
        BorderPane.setMargin(vbox, new Insets(3));
        splitPane.getItems().add(borderPaneWrapper);

        Node masterEntityEditorWidgetContent = dataEditor.createContent();
        SplitPane.setResizableWithParent(masterEntityEditorWidgetContent, Boolean.FALSE);
        splitPane.getItems().add(masterEntityEditorWidgetContent);
//        pane.setDetailSide(Side.RIGHT);
//        pane.setShowDetailNode(true);
        splitPane.setDividerPositions(dividerPosition);
//        pane.setAnimated(true);

//        Button newButton = new Button("new");
//        newButton.setOnAction(event -> {
//            newItemAdder.addNewItem();
//        });
//        selectDataListWidget.getFilterHBox().getChildren().add(newButton);
        return splitPane;
    }

    public MasterDetailWidget<T> setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }
}
