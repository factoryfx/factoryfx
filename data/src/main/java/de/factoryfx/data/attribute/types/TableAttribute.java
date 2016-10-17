package de.factoryfx.data.attribute.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

//** usually its a bad idea to use this Attribute type cause its  not typed*/
public class TableAttribute extends ValueAttribute<TableAttribute.Table> {

    public static class Table{
        @JsonProperty
        final List<TableRow> rows = new ArrayList<>();

        @JsonProperty
        final List<String> columnHeaders = new ArrayList<>();

        public Table deleteColumn() {
            for (TableRow row: rows){
                if (!row.columns.isEmpty()) {
                    row.columns.remove(row.columns.size()-1);
                }
            }
            return this;
        }

        public Table deleteRow() {
            if (!rows.isEmpty()) {
                rows.remove(rows.size()-1);
            }
            return this;
        }

        @JsonIgnore
        public int getRowCount(){
            return rows.size();
        }

        @JsonIgnore
        public int getColCount(){
            if (rows.isEmpty()){
                return 0;
            }
            return rows.get(0).columns.size();
        }

        @JsonIgnore
        public String getCellValue(int row, int col){
            return rows.get(row).columns.get(col);
        }

        @JsonIgnore
        public boolean isEmpty() {
            return rows.isEmpty();
        }

        @JsonIgnore
        public Table setCellValue(int row, int col, String value){
            rows.get(row).columns.set(col,value);
            return this;
        }

        public Table copy(){
            Table table = new Table();
            for (TableRow tableRow: rows){
                table.rows.add(tableRow.copy());
            }
            return table;
        }

        public Table addRow(){
            TableRow tableRow = new TableRow();
            if (!rows.isEmpty()){
                for (String dummy: rows.get(0).columns){
                    tableRow.columns.add("");
                }
            } else {
                tableRow.columns.add("");
            }
            rows.add(tableRow);
            return this;
        }

        @JsonIgnore
        public TableRow getLastRow(){
            return rows.get(rows.size()-1);
        }

        public Table addColumn(){
            for (TableRow tableRow: rows){
                tableRow.columns.add("");
            }
            return this;
        }

        @JsonIgnore
        public Table setColumnHeaders(String... headers){
            this.columnHeaders.clear();
            this.columnHeaders.addAll(Arrays.asList(headers));
            return this;
        }

        @JsonIgnore
        public List<String> getColumnHeaders(){
            return new ArrayList<>(columnHeaders);
        }
    }

    public static class TableRow{
        @JsonProperty
        final  List<String> columns = new ArrayList<>();

        public void clear() {
            columns.clear();
        }

        public TableRow copy() {
            TableRow tableRow = new TableRow();
            tableRow.columns.addAll(columns);
            return tableRow;
        }

        public TableRow add(String value) {
            columns.add(value);
            return this;
        }
    }

    public TableAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Table.class);
        defaultValue(new Table());
    }

    @JsonCreator
    TableAttribute(Table initialValue) {
        super(null,Table.class);
        set(initialValue);
    }

    public void copy(Table value){
        set(value.copy());
    }
}
