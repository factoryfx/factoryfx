package de.factoryfx.javafx.data.widget.tableview;

import de.factoryfx.data.Data;
import javafx.collections.ObservableList;

public class TableDataView<T extends Data> {

    ObservableList<T> dataList;

    //TO define observable to refresh list similar to bindings
    public TableDataView(ObservableList<T> dataList) {
        this.dataList = dataList;
    }

    public ObservableList<T> dataList(){
        return dataList;
    }

}
