package de.factoryfx.guimodel;

import java.util.Arrays;
import java.util.List;

public class Table<T> {
    public final List<TableColumn<T>> tableColumn;

    public Table(List<TableColumn<T>> tableColumn) {
        this.tableColumn = tableColumn;
    }

    @SafeVarargs
    public Table(TableColumn<T>... tableColumn) {
        this.tableColumn = Arrays.asList(tableColumn);
    }
}
