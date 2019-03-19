package io.github.factoryfx.javafx.data.widget.dataview;

import java.util.List;
import java.util.function.Supplier;

import io.github.factoryfx.data.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UpdatableDataView<T extends Data> implements DataView<T> {

    private final ObservableList<T> dataList= FXCollections.observableArrayList();
    private final Supplier<List<T>> listSupplier;
    public UpdatableDataView(Supplier<List<T>> listSupplier) {
        this.listSupplier = listSupplier;
    }


    public void update(){
        dataList.setAll(listSupplier.get());
    }

    @Override
    public ObservableList<T> dataList(){
        update();
        return dataList;
    }

}
