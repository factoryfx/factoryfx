package io.github.factoryfx.javafx.factory.widget.dataview;

import io.github.factoryfx.factory.FactoryBase;
import javafx.collections.ObservableList;

public interface DataView<T extends FactoryBase<?,?>> {

    ObservableList<T> dataList();
}
