package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.TableAttribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.glyphfont.FontAwesome;

public class TableAttributeVisualisation implements AttributeEditorVisualisation<TableAttribute.Table> {
    private final UniformDesign uniformDesign;
    private BooleanBinding tableHasColumns;

    public TableAttributeVisualisation(UniformDesign uniformDesign){
        this.uniformDesign=uniformDesign;
    }


    @Override
    public Node createContent(SimpleObjectProperty<TableAttribute.Table> boundTo) {
        VBox detailView = createDetailView(boundTo);
        return uniformDesign.createExpandableEditorWrapper(boundTo, detailView, FontAwesome.Glyph.TH, table -> table.getRowCount()+"x"+table.getColCount());
    }

    private VBox createDetailView(SimpleObjectProperty<TableAttribute.Table> boundTo) {
        SpreadsheetView spreadsheetView = new SpreadsheetView();

        InvalidationListener listener = observable -> {
            TableAttribute.Table table = boundTo.get();
            if (table != null) {
                GridBase grid = new GridBase(table.getRowCount(), table.getColCount());
                ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
                for (int row = 0; row < grid.getRowCount(); ++row) {
                    final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
                    for (int column = 0; column < grid.getColumnCount(); ++column) {
                        SpreadsheetCell value = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, table.getCellValue(row, column));
                        final int rowFinal=row;
                        final int colFinal=column;
                        value.textProperty().addListener((observable1, oldValue, newValue) -> {
                            boundTo.get().setCellValue(rowFinal, colFinal,newValue);
                        });
                        list.add(value);
                    }
                    rows.add(list);
                }
                grid.setRows(rows);
                spreadsheetView.setGrid(grid);
                for (String header: table.getColumnHeaders()) {
                    spreadsheetView.getGrid().getColumnHeaders().add(header);
                }
            } else {
                spreadsheetView.setGrid(new GridBase(0,0));
            }

        };
        boundTo.addListener(listener);
        listener.invalidated(boundTo);

        VBox root = new VBox(3);
        HBox buttons = new HBox(3);
        buttons.setAlignment(Pos.CENTER_LEFT);
        {
            Button addRow = new Button("");
            uniformDesign.addIcon(addRow, FontAwesome.Glyph.PLUS);
            addRow.setOnAction(e -> boundTo.set(new TableAttribute.Table()));
            buttons.getChildren().add(addRow);
            addRow.disableProperty().bind(boundTo.isNotNull());
        }
        buttons.getChildren().add(new Label("row:"));
        {
            Button addRow = new Button();
            addRow.setTooltip(new Tooltip("add row"));
            uniformDesign.addIcon(addRow, FontAwesome.Glyph.PLUS);
            addRow.setOnAction(e -> boundTo.set(boundTo.get().copy().addRow()));
            addRow.disableProperty().bind(boundTo.isNull());
            buttons.getChildren().add(addRow);
        }
        {
            Button deleteRow = new Button();
            deleteRow.setTooltip(new Tooltip("delete row"));
            uniformDesign.addDangerIcon(deleteRow, FontAwesome.Glyph.MINUS);
            deleteRow.setOnAction(e -> boundTo.set(boundTo.get().copy().deleteRow()));
            deleteRow.disableProperty().bind(boundTo.isNull());
            buttons.getChildren().add(deleteRow);
        }
        tableHasColumns = Bindings.createBooleanBinding(() -> boundTo.get() != null && boundTo.get().getRowCount() > 0, boundTo);
        buttons.getChildren().add(new Label("column:"));
        {
            Button addColum = new Button();
            addColum.setTooltip(new Tooltip("add column"));
            uniformDesign.addIcon(addColum, FontAwesome.Glyph.PLUS);
            addColum.setOnAction(e -> boundTo.set(boundTo.get().copy().addColumn()));
            addColum.disableProperty().bind(boundTo.isNull().or(tableHasColumns.not()));
            buttons.getChildren().add(addColum);
        }
        {
            Button deleteColum = new Button();
            deleteColum.setTooltip(new Tooltip("delete column"));
            uniformDesign.addDangerIcon(deleteColum, FontAwesome.Glyph.MINUS);
            deleteColum.setOnAction(e -> boundTo.set(boundTo.get().copy().deleteColumn()));
            deleteColum.disableProperty().bind(boundTo.isNull().or(tableHasColumns.not()));
            buttons.getChildren().add(deleteColum);
        }
        buttons.getChildren().add(new Label("import:"));
        {
            Button excelButton = new Button("");
            uniformDesign.addIcon(excelButton, FontAwesome.Glyph.FILE_EXCEL_ALT);
            excelButton.setOnAction(e -> {
                Clipboard.getSystemClipboard().getContentTypes();
                String excelCsv = Clipboard.getSystemClipboard().getString();

                TableAttribute.Table table = new TableAttribute.Table();
                for (String row: excelCsv.split("\\n")){
                    if (table.getColumnHeaders().isEmpty()){
                        table.setColumnHeaders(row.split("\\t"));
                    } else {
                        table.addRow();
                        table.getLastRow().clear();
                        for(String col: row.split("\\t")){
                            table.getLastRow().add(col);
                        }
                    }
                }
                boundTo.set(table);

            });
            excelButton.disableProperty().bind(boundTo.isNull());
            buttons.getChildren().add(excelButton);
        }

        root.getChildren().addAll(spreadsheetView,buttons);
        return root;
    }
}
