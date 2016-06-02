package de.factoryfx.richclient;

import java.util.HashMap;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.AttributeChangeListener;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.richclient.framework.editor.MasterEditor;
import de.factoryfx.richclient.framework.view.FactoryView;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;

public class GenericTreeFactoryViewRichClient<T extends FactoryBase<? extends LiveObject, T>> extends FactoryView<T> {


    public GenericTreeFactoryViewRichClient() {
    }

    @Override
    public Node createContent() {
//        if (object!=null){
//            return object.metadata.displayName+": "+object.get();
//        }

        TreeView<Attribute<?>> tree = new TreeView<>();
        tree.setCellFactory(param -> new TextFieldTreeCell<Attribute<?>>() {
            AttributeChangeListener attributeChangeListener = attribute -> updateText((Attribute<?>) attribute);

            @Override
            public void updateItem(Attribute<?> item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    item.addListener(attributeChangeListener);
                    updateText(item);
                } else {
                    this.setText("");
                }
                //CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
            }

            private void updateText(Attribute<?> attribute) {
                if (attribute != null) {
                    setText(attribute.metadata.displayName + ": " + attribute.get());
                }
            }
        });


        ChangeListener<T> changeListener = (observable, oldValue, newValue) -> {
            HashMap<FactoryBase<?,?>,TreeItem<Attribute<?>>> factoryToTreeItem= new HashMap<>();

            addOrGetTreeItem(newValue,factoryToTreeItem).setValue(new ReferenceAttribute<>(new AttributeMetadata<T>("root")));

            if (newValue!=null){
                newValue.visitAttributesFlat(new FactoryBase.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?> value, FactoryBase<?,?> parent) {
                        addOrGetTreeItem(parent,factoryToTreeItem).getChildren().add(new TreeItem<>(value));
                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference, FactoryBase<?,?> parent) {
                        TreeItem<Attribute<?>> treeItem = addOrGetTreeItem(reference.get(), factoryToTreeItem);
                        treeItem.setValue(reference);
                        addOrGetTreeItem(parent,factoryToTreeItem).getChildren().add(treeItem);
                        reference.getOptional().ifPresent(r->r.visitAttributesFlat(this));
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList, FactoryBase<?,?> parent) {
                        referenceList.forEach(r -> {
                            TreeItem<Attribute<?>> treeItem = addOrGetTreeItem(r, factoryToTreeItem);
                            treeItem.setValue(referenceList);
                            addOrGetTreeItem(parent, factoryToTreeItem).getChildren().add(treeItem);
                        });
                        referenceList.forEach(r->r.visitAttributesFlat(this));
                    }
                });

                tree.setRoot(addOrGetTreeItem(newValue,factoryToTreeItem));


            } else {

            }
        };
        rootFactory.addListener(changeListener);
        changeListener.changed(rootFactory,rootFactory.get(),rootFactory.get());

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(tree);
        MasterEditor masterEditor = new MasterEditor();
        borderPane.setCenter(masterEditor.createContent());

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue!=null){
                masterEditor.unbind(oldValue.getValue());
            }
            masterEditor.bind(newValue.getValue());
        });
        return borderPane;
    }

    private TreeItem<Attribute<?>> addOrGetTreeItem(FactoryBase<?,?> factory , HashMap<FactoryBase<?,?>,TreeItem<Attribute<?>>> factoryToTreeItem){
        TreeItem<Attribute<?>> result = factoryToTreeItem.get(factory);
        if (result==null){
            result = new TreeItem<>();
            factoryToTreeItem.put(factory,result);
        }
        return result;
    }
}
