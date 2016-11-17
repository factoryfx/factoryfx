package de.factoryfx.javafx.widget.datatreeview;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DataTreeView<T extends Data> {

    private final ObservableList<T> dataList= FXCollections.observableArrayList();
    private final Supplier<List<T>> listSupplier;
    private final Function<T,TreeItem<Data>> treeSupplier;
    public DataTreeView(Supplier<List<T>> listSupplier, Function<T,TreeItem<Data>> treeSupplier) {
        this.listSupplier = listSupplier;
        this.treeSupplier = treeSupplier;
    }

    Optional<Consumer<TreeItem<Data>>> updateAction=Optional.empty();
    public void setUpdateAction(Consumer<TreeItem<Data>> updateAction) {
        this.updateAction= Optional.of(updateAction);
    }

    public void update(){
        dataList.setAll(listSupplier.get());
        updateAction.ifPresent(treeItemConsumer -> treeItemConsumer.accept(dataTree()));
    }


    private TreeItem<Data> dataTree(){
        TreeItem<Data>  root = new TreeItem<>(null);
        dataList.forEach(item -> {
            final TreeItem<Data> treeItem = treeSupplier.apply(item);
            root.getChildren().add(treeItem);

        });
        return root;
    }

}
