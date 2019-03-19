package io.github.factoryfx.javafx.data.widget.dataview;

import io.github.factoryfx.data.Data;
import javafx.collections.ObservableList;

public interface DataView<T extends Data> {

    ObservableList<T> dataList();
}
