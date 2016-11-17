package de.factoryfx.javafx.widget.datatreeview;

import java.util.function.Consumer;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;

public class DataTreeViewWidget implements CloseAwareWidget {
    private final DataTreeView dataTreeView;
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private final UniformDesign uniformDesign;

    public DataTreeViewWidget(DataTreeView dataTreeView, DataEditor dataEditor, UniformDesign uniformDesign) {
        this.dataTreeView = dataTreeView;
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public void closeNotifier() {
//        listener.changed(null, null, null);
    }

    @Override
    public Node createContent() {
//        MasterDetailPane pane = new MasterDetailPane();
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        TreeView<Data> tree = new TreeView<>();
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tree.setCellFactory(param -> new TextFieldTreeCell<Data>() {
            @Override
            public void updateItem(Data item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    this.setText(item.internal().getDisplayText());
                }
                //CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
            }

        });
        tree.setShowRoot(false);

        SplitPane.setResizableWithParent(tree, Boolean.FALSE);
        splitPane.getItems().add(tree);

        Node dataEditorWidget = this.dataEditor.createContent();
        SplitPane.setResizableWithParent(dataEditorWidget, Boolean.TRUE);
        splitPane.getItems().add(dataEditorWidget);
        splitPane.setDividerPositions(dividerPosition);

        dataEditor.reset();
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                dataEditor.edit(newValue.getValue());
                dataEditor.resetHistory();
            }
        });


        dataTreeView.setUpdateAction(new Consumer<TreeItem<Data>>() {
            @Override
            public void accept(TreeItem<Data> dataTreeItem) {
                tree.setRoot(dataTreeItem);
            }
        });
        dataTreeView.update();

        return splitPane;
    }

    public DataTreeViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }
}
