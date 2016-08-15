package de.factoryfx.adminui.angularjs.integration.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.adminui.angularjs.model.table.WebGuiTable;

public class VisitorToTables implements Function<ExampleVisitor,List<WebGuiTable>> {

    @Override
    public List<WebGuiTable> apply(ExampleVisitor exampleVisitor) {

        ArrayList<WebGuiTable> webGuiTables = new ArrayList<>();
        ArrayList<List<String>> rows = new ArrayList<>();

        ArrayList<String> tableColumns = new ArrayList<>();
        exampleVisitor.exampleDates.forEach(exampleData -> {
            ArrayList<String> row = new ArrayList<>();
            row.add(exampleData.data1);
            row.add(exampleData.data2);
            row.add(exampleData.data3);
            rows.add(row);
        });

        tableColumns.add("data1");
        tableColumns.add("data2");
        tableColumns.add("data3");
        webGuiTables.add(new WebGuiTable("Example data Table", tableColumns, rows));


        return webGuiTables;
    }
}
