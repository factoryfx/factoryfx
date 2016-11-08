package de.factoryfx.javafx.widget.tree;

import java.util.function.Consumer;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;

public class DataTreeWidget implements CloseAwareWidget {
    private final Data root;
    private final DataEditor dataEditor;

    public DataTreeWidget(DataEditor dataEditor, Data root) {
        this.dataEditor=dataEditor;
        this.root = root;
    }

    @Override
    public void closeNotifier() {
//        listener.changed(null, null, null);
    }

    @Override
    public Node createContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(dataEditor.createContent(),createTree());
        splitPane.setDividerPosition(0,0.75);
        return splitPane;
    }

    private Node createTree(){
        TreeView<Data> tree = new TreeView<>();
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tree.setCellFactory(param -> new TextFieldTreeCell<Data>() {
            @Override
            public void updateItem(Data item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    this.setText(item.internal().getDisplayText());
                } else {
                    this.setText("<empty>");
                }
                //CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
            }

        });
        tree.setRoot(constructTree(root));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                dataEditor.edit(newValue.getValue());
            }
        });

        ChangeListener<Data> dataChangeListener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {//javafx bug workaround http://stackoverflow.com/questions/26343495/indexoutofboundsexception-while-updating-a-listview-in-javafx
                TreeItem<Data> treeItemRoot = constructTree(root);
                tree.setRoot(treeItemRoot);

                tree.getSelectionModel().clearSelection();
                for (TreeItem<Data> item : treeViewTraverser.breadthFirstTraversal(treeItemRoot)) {
                    if (item.getValue() == newValue) {
                        tree.getSelectionModel().select(item);
                    }
                }
            });

        };
        dataEditor.editData().addListener(dataChangeListener);
        dataChangeListener.changed(dataEditor.editData(),dataEditor.editData().get(),dataEditor.editData().get());

        ScrollPane scrollPane = new ScrollPane(tree);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("expand all");
        menuItem.setOnAction(event -> {
            for (TreeItem<Data> item : treeViewTraverser.breadthFirstTraversal(tree.getRoot())) {
                item.setExpanded(true);
            }
        });
        contextMenu.getItems().addAll(menuItem);
        tree.setContextMenu(contextMenu);

        return scrollPane;
    }

    TreeTraverser<TreeItem<Data>> treeViewTraverser = new TreeTraverser<TreeItem<Data>>() {
        @Override
        public Iterable<TreeItem<Data>> children(TreeItem<Data> data) {
            return data.getChildren();
        }
    };

    private TreeItem<Data> constructTree(Data data){
        TreeItem<Data> dataTreeItem = new TreeItem<>(data);

        if (data!=null){
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?> value) {

                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference) {
                        dataTreeItem.getChildren().add(constructTree(reference.get()));
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
//                        TreeItem<Data> listDataTreeItem = new TreeItem<>(data);
//                        listDataTreeItem
//                                new type for data?
//                        dataTreeItem.getChildren().add(listDataTreeItem);
                        referenceList.get().forEach(new Consumer<Data>() {
                            @Override
                            public void accept(Data data1) {
                                dataTreeItem.getChildren().add(constructTree(data1));
                            }
                        });
                    }
                });
            });
        }



        data.internal().visitChildFactoriesFlat(child -> {
            dataTreeItem.getChildren().add(constructTree(child));
        });
        return dataTreeItem;
    }


}
