package de.factoryfx.example.factory;

import java.util.List;
import java.util.function.Function;


public class OrderCollectorToTables implements Function<OrderCollector,List<Object>> {

    @Override
    public List<Object> apply(OrderCollector visitor) {

//        ArrayList<WebGuiTable> webGuiTables = new ArrayList<>();
//        ArrayList<List<String>> rows = new ArrayList<>();
//
//        ArrayList<String> tableColumns = new ArrayList<>();
//        visitor.getOrders().forEach(exampleData -> {
//            ArrayList<String> row = new ArrayList<>();
//            row.add(exampleData.customerName);
//            row.add(exampleData.productName);
//            rows.add(row);
//        });
//
//        tableColumns.add("customerName");
//        tableColumns.add("productName");
//        webGuiTables.add(new WebGuiTable("Orders", tableColumns, rows));


        return null;
    }
}
