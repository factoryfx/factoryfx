package de.factoryfx.development.angularjs.model.table;

import java.util.List;

public class WebGuiTable {
    public final String title;
    public final List<String> tableColumns;
    public final List<List<String>> tableRows;

    public WebGuiTable(String title, List<String> tableColumns, List<List<String>> tableRows) {
        this.title=title;
        this.tableColumns = tableColumns;
        this.tableRows = tableRows;
    }
}
