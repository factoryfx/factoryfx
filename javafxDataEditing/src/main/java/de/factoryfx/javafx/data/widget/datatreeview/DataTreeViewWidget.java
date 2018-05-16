package de.factoryfx.javafx.data.widget.datatreeview;

import com.google.common.graph.Traverser;
import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.DataTextFieldTreeCell;
import de.factoryfx.javafx.data.widget.Widget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class DataTreeViewWidget<T extends Data> implements Widget {
    private final DataTreeView<T> dataTreeView;
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;

    public DataTreeViewWidget(DataTreeView<T> dataTreeView, DataEditor dataEditor) {
        this.dataTreeView = dataTreeView;
        this.dataEditor = dataEditor;
    }

    @Override
    public Node createContent() {
        dataEditor.reset();

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        TreeView<Data> tree = new TreeView<>();
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tree.setCellFactory(param -> new DataTextFieldTreeCell<>(data->data,null));
        tree.setShowRoot(false);


        SplitPane.setResizableWithParent(tree, Boolean.FALSE);
        splitPane.getItems().add(tree);

        Node dataEditorWidget = this.dataEditor.createContent();
        SplitPane.setResizableWithParent(dataEditorWidget, Boolean.TRUE);
        splitPane.getItems().add(dataEditorWidget);
        splitPane.setDividerPositions(dividerPosition);

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                dataEditor.edit(newValue.getValue());
                dataEditor.resetHistory();
            }
        });


        dataTreeView.setUpdateAction(dataTreeItem -> {
            tree.setRoot(dataTreeItem);
            for (TreeItem<Data> treeItem: treeTraverser.breadthFirst(dataTreeItem)){
                if (treeItem.getValue()==dataEditor.editData().get()){
                    tree.getSelectionModel().select(treeItem);
                }
            }
        });
        dataTreeView.update();

        return splitPane;
    }

    Traverser<TreeItem<Data>> treeTraverser = Traverser.forGraph(TreeItem::getChildren);

    public DataTreeViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }

    public ReadOnlyObjectProperty<Data> selectedItem(){
        return dataEditor.editData();
    }

}
