package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import javafx.collections.ObservableList;

public class DataView {

    ObservableList<? extends Data> dataList;

    //TO define observable to refresh list similar to bindings
    public DataView(ObservableList<? extends Data> dataList) {
        this.dataList = dataList;
    }

    public ObservableList<? extends Data> dataList(){
        return dataList;
    }

}
