package io.github.factoryfx.javafx.widget.factory.tree;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import com.google.common.graph.Traverser;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.DataTextFieldTreeCell;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;

/** Tree and detail view to edit factories, factory tree in treeView and detail editor*/
public class DataTreeWidget implements Widget {
    private FactoryBase<?,?> root;
    private final DataEditor dataEditor;
    private final UniformDesign uniformDesign;
    private final SplitPane splitPane = new SplitPane();

    public DataTreeWidget(DataEditor dataEditor, UniformDesign uniformDesign) {
        this.dataEditor=dataEditor;
        this.uniformDesign = uniformDesign;
    }

    public void edit(FactoryBase<?,?> root){
        this.root=root;
        splitPane.getItems().setAll(dataEditor.createContent(),createTree());
        splitPane.setDividerPosition(0,0.75);
        dataEditor.edit(root);
    }

    @Override
    public Node createContent() {
        return splitPane;
    }

    private boolean programmaticallySelect=false;
    private boolean disableChangeListenerSelect=false;

    ChangeListener<FactoryBase<?,?>> dataChangeListener;
    private Node createTree(){
        TreeView<TreeData> tree = new TreeView<>();
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tree.setCellFactory(param -> new DataTextFieldTreeCell<>(TreeData::getData, TreeData::getDisplayText));
        tree.setRoot(constructTreeFromRoot());

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null && !programmaticallySelect && newValue.getValue()!=null && newValue.getValue().getData()!=null){
                try {
                    disableChangeListenerSelect=true;
                    dataEditor.edit(newValue.getValue().getData());
                } finally {
                    disableChangeListenerSelect=false;
                }
            }
        });

        dataChangeListener = (observable, oldValue, newValue) -> {
            if (!disableChangeListenerSelect){
                Platform.runLater(() -> {//javafx bug workaround http://stackoverflow.com/questions/26343495/indexoutofboundsexception-while-updating-a-listview-in-javafx
                    if (treeStructureChanged(root)) {
                        TreeItem<TreeData> treeItemRoot = constructTreeFromRoot();
                        tree.setRoot(treeItemRoot);
                    }

                    tree.getSelectionModel().clearSelection();
                    for (TreeItem<TreeData> item : treeViewTraverser.breadthFirst(tree.getRoot())) {
                        if (item.getValue().match(newValue)) {
                            programmaticallySelect = true;
                            tree.getSelectionModel().select(item);
                            programmaticallySelect = false;
                            break;
                        }
                    }
                });
            }
        };
        dataEditor.editData().addListener(new WeakChangeListener<>(dataChangeListener));
        dataChangeListener.changed(dataEditor.editData(),dataEditor.editData().get(),dataEditor.editData().get());

        ScrollPane scrollPane = new ScrollPane(tree);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("expand all");
        menuItem.setOnAction(event -> {
            if (tree.getRoot().getValue().getData().internal().collectChildrenDeep().size()<200){
                for (TreeItem<TreeData> item : treeViewTraverser.breadthFirst(tree.getRoot())) {
                    item.setExpanded(true);
                }
            }
        });
        contextMenu.getItems().addAll(menuItem);
        tree.setContextMenu(contextMenu);

        return scrollPane;
    }

    private final Traverser<TreeItem<TreeData>> treeViewTraverser =  Traverser.forTree(TreeItem::getChildren);

    long lastSize=0;
    private boolean treeStructureChanged(FactoryBase<?,?> root){
        if (root!=null) {
            return lastSize != root.internal().collectChildrenDeep().size();
        }
        return false;
    }


    private TreeItem<TreeData> constructTreeFromRoot(){
        lastSize=root.internal().collectChildrenDeep().size();
        return constructTree(root);
    }

    private TreeItem<TreeData> constructTree(FactoryBase<?,?> data){
        if (data!=null){
            TreeItem<TreeData> dataTreeItem = new TreeItem<>(new TreeData(data,null));
            data.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
                if (attribute instanceof FactoryBaseAttribute) {
                    FactoryBase<?,?> child=((FactoryBaseAttribute<?,?,?>)attribute).get();
                    if (child!=null){
                        TreeItem<TreeData> refDataTreeItem = new TreeItem<>(new TreeData(null,uniformDesign.getLabelText(attribute,attributeMetadata.attributeVariableName)));
                        dataTreeItem.getChildren().add(refDataTreeItem);

                        final TreeItem<TreeData> treeItem = constructTree(child);
                        if (treeItem!=null){
                            refDataTreeItem.getChildren().add(treeItem);
                        }
                    }
                }
                if (attribute instanceof FactoryListBaseAttribute) {
                    FactoryListBaseAttribute<?, ?, ?> factoryListBaseAttribute = (FactoryListBaseAttribute<?, ?, ?>) attribute;
                    if (!factoryListBaseAttribute.isEmpty()){
                        TreeItem<TreeData> refDataTreeItem = new TreeItem<>(new TreeData(null,uniformDesign.getLabelText(attribute,attributeMetadata.attributeVariableName)));
                        dataTreeItem.getChildren().add(refDataTreeItem);
                        factoryListBaseAttribute.forEach(child -> {
                            final TreeItem<TreeData> treeItem = constructTree(child);
                            if (treeItem!=null){
                                refDataTreeItem.getChildren().add(treeItem);
                            }
                        });
                    }
                }
            });
            return dataTreeItem;
        }
        return null;
    }

    public static class TreeData{
        private final FactoryBase<?,?> data;
        private final String text;

        public TreeData(FactoryBase<?,?> data, String text) {
            this.data = data;
            this.text = text;
        }

        public String getDisplayText() {
            if (text==null && data!=null){
                return data.internal().getDisplayText();
            }
            return text;
        }


        public FactoryBase<?,?> getData(){
            return data;
        }

        public boolean match(FactoryBase<?,?> newValue) {
            return text==null && newValue==data;
        }
    }

    @Override
    public void destroy() {
        dataEditor.destroy();
    }

}
