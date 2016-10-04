package de.factoryfx.javafx.widget.masterdetail;

import de.factoryfx.data.Data;
import javafx.collections.ObservableList;

public class DataView {

    ObservableList<Data> dataList;

    //TO define observable to refresh list similar to bindings
    public DataView(ObservableList<Data> dataList) {
        this.dataList = dataList;
    }

    public ObservableList<Data> dataList(){
        return dataList;
    }

}
