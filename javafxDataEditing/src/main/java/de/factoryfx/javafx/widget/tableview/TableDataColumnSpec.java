package de.factoryfx.javafx.widget.tableview;

import de.factoryfx.data.Data;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

public class TableDataColumnSpec<T extends Data> {

    public final String columnName;
    public final Function<T,ObservableValue<String>> cellValueProvider;

    public TableDataColumnSpec(String columnName, Function<T, ObservableValue<String>> cellValueProvider) {
        this.columnName = columnName;
        this.cellValueProvider = cellValueProvider;
    }
}
