package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import javafx.collections.ObservableList;

public interface DataView<T extends Data> {

    ObservableList<T> dataList();
}
