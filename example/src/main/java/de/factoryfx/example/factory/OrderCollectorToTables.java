package de.factoryfx.example.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.server.angularjs.model.table.WebGuiTable;

public class OrderCollectorToTables implements Function<OrderCollector,List<WebGuiTable>> {

    @Override
    public List<WebGuiTable> apply(OrderCollector visitor) {

        ArrayList<WebGuiTable> webGuiTables = new ArrayList<>();
        ArrayList<List<String>> rows = new ArrayList<>();

        ArrayList<String> tableColumns = new ArrayList<>();
        visitor.getOrders().forEach(exampleData -> {
            ArrayList<String> row = new ArrayList<>();
            row.add(exampleData.customerName);
            row.add(exampleData.productName);
            rows.add(row);
        });

        tableColumns.add("customerName");
        tableColumns.add("productName");
        webGuiTables.add(new WebGuiTable("Orders", tableColumns, rows));


        return webGuiTables;
    }
}
