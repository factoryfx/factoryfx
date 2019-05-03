package io.github.factoryfx.javafx.widget.factory.masterdetail;

import io.github.factoryfx.factory.FactoryBase;
import javafx.collections.ObservableList;

public interface DataView<T extends FactoryBase<?,?>> {

    ObservableList<T> dataList();
}
