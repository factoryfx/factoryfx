package de.factoryfx.adminui;

public class FactoryTreeEditor{//<T extends FactoryBase<? extends LiveObject, T>> extends FactoryView<T> {
//
//
//    public FactoryTreeEditor() {
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Node createContent() {
//
//        TreeView<Attribute<?,?>> tree = new TreeView<>();
//        tree.setCellFactory(param -> new TextFieldTreeCell<Attribute<?,?>>() {
//            final AttributeChangeListener attributeChangeListener = (attribute, value) -> updateText((Attribute<?,?>) attribute);
//            Attribute<?,?> attribute;
//            @Override
//            public void updateItem(Attribute<?,?> item, boolean empty) {
//                super.updateItem(item, empty);
//                if (attribute!=null){
//                    attribute.removeListener(attributeChangeListener);
//                }
//                attribute=item;
//                if (attribute != null) {
//                    attribute.addListener(attributeChangeListener);
//                    updateText(attribute);
//                } else {
//                    this.setText("");
//                }
//                //CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
//            }
//
//            private void updateText(Attribute attribute) {
//                if (attribute != null) {//TODO locale
//                    setText(attribute.metadata.labelText + ": " + attribute.get());
//                }
//            }
//        });
//
//
//        ChangeListener<T> changeListener = (observable, oldValue, newValue) -> {
//            HashMap<FactoryBase<?,?>,TreeItem<Attribute<?,?>>> factoryToTreeItem= new HashMap<>();
//
//            addOrGetTreeItem(newValue,factoryToTreeItem).setValue(new ReferenceAttribute<>(null,new AttributeMetadata()));//"root"
//
//            if (newValue!=null){
//                HashSet<FactoryBase<?, ?>> allModelEntities = new HashSet<>();
//                newValue.collectModelEntitiesTo(allModelEntities);
//
//                for (FactoryBase<?, ?> factoryBase: allModelEntities){
//                    factoryBase.visitAttributesFlat((attributeVariableName, attribute) -> {
//                        attribute.visit(new Attribute.AttributeVisitor() {
//                            @Override
//                            public void value(Attribute<?,?> value) {
//                                addOrGetTreeItem(factoryBase,factoryToTreeItem).getChildren().add(new TreeItem<>(value));
//                            }
//
//                            @Override
//                            public void reference(ReferenceAttribute<?,?> reference) {
//                                TreeItem<Attribute<?,?>> treeItem = addOrGetTreeItem(factoryBase, factoryToTreeItem);
//                                treeItem.setValue(reference);
//                                addOrGetTreeItem(factoryBase,factoryToTreeItem).getChildren().add(treeItem);
//                            }
//
//                            @Override
//                            public void referenceList(ReferenceListAttribute<?,?> referenceList) {
//                                referenceList.forEach(r -> {
//                                    TreeItem<Attribute<?,?>> treeItem = addOrGetTreeItem(r, factoryToTreeItem);
//                                    treeItem.setValue(referenceList);
//                                    addOrGetTreeItem(factoryBase, factoryToTreeItem).getChildren().add(treeItem);
//                                });
//                            }
//                        });
//                    });
//                }
//
//                tree.setRoot(addOrGetTreeItem(newValue,factoryToTreeItem));
//
//
//            } else {
//
//            }
//        };
//        rootFactory.addListener(changeListener);
//        changeListener.changed(rootFactory,rootFactory.get(),rootFactory.get());
//
//        BorderPane borderPane = new BorderPane();
//        borderPane.setLeft(tree);
//        MasterEditor masterEditor = new MasterEditor();
//        borderPane.setCenter(masterEditor.createContent());
//
//        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue!=null){
//                masterEditor.unbind(oldValue.getValue());
//            }
//            masterEditor.bind(newValue.getValue());
//        });
//        return borderPane;
//    }
//
//    private TreeItem<Attribute<?,?>> addOrGetTreeItem(FactoryBase<?,?> factory , HashMap<FactoryBase<?,?>,TreeItem<Attribute<?,?>>> factoryToTreeItem){
//        TreeItem<Attribute<?,?>> result = factoryToTreeItem.get(factory);
//        if (result==null){
//            result = new TreeItem<>();
//            factoryToTreeItem.put(factory,result);
//        }
//        return result;
//    }
}
