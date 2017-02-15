package de.factoryfx.javafx.widget.datatreeview;

import java.util.function.Consumer;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.DataTextFieldTreeCell;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
        tree.setCellFactory(param -> new DataTextFieldTreeCell<>(data->data,null));
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
            @SuppressWarnings("unchecked")
            public void accept(TreeItem<Data> dataTreeItem) {
                tree.setRoot(dataTreeItem);
                for (TreeItem treeItem: treeTraverser.breadthFirstTraversal(dataTreeItem)){
                    if (treeItem.getValue()==dataEditor.editData().get()){
                        tree.getSelectionModel().select(treeItem);
                    }
                }
            }
        });
        dataTreeView.update();

        return splitPane;
    }

    TreeTraverser<TreeItem<Data>> treeTraverser = new TreeTraverser<TreeItem<Data>>() {
        @Override
        public Iterable<TreeItem<Data>> children(TreeItem<Data> data) {
            return data.getChildren();
        }
    };

    public DataTreeViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }

    public ReadOnlyObjectProperty<Data> selectedItem(){
        return dataEditor.editData();
    }

}
