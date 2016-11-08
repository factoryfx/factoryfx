package de.factoryfx.javafx.widget.dataview;

import java.util.List;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataView {

    private final ObservableList<Data> dataList= FXCollections.observableArrayList();
    private final Supplier<List<Data>> listSupplier;
    public DataView(Supplier<List<Data>> listSupplier) {
        this.listSupplier = listSupplier;
    }

    public void update(){
        dataList.setAll(listSupplier.get());
    }

    public ObservableList<Data> dataList(){
        update();
        return dataList;
    }

}
