package de.factoryfx.javafx.data.widget.tableview;

import java.util.function.Function;

import de.factoryfx.data.Data;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

public class TableDataColumnSpec<T extends Data> {

    private final String columnName;
    private final Function<T,ObservableValue<String>> cellValueProvider;
    private final String cssColumnClass;

    public TableDataColumnSpec(String columnName, Function<T, ObservableValue<String>> cellValueProvider, String cssColumnClass) {
        this.columnName = columnName;
        this.cellValueProvider = cellValueProvider;
        this.cssColumnClass = cssColumnClass;
    }

    public TableColumn<T,String> create(){
        TableColumn<T, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(param->cellValueProvider.apply(param.getValue()));
        if (cssColumnClass!=null){
            column.getStyleClass().add(cssColumnClass);
        }
        return column;
    }

}
