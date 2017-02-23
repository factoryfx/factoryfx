package de.factoryfx.javafx.widget.table;

import de.factoryfx.javafx.util.UniformDesign;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.controlsfx.glyphfont.FontAwesome;

public class TableMenu<T> {
    private final UniformDesign uniformDesign;

    public TableMenu(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    public TableView<T> addMenu(TableView<T> tableView){

        tableView.setTableMenuButtonVisible(true);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem item = new MenuItem("Copy cell",uniformDesign.createIcon(FontAwesome.Glyph.COPY));
        item.setOnAction(event -> {
            copyTableCell(tableView);
        });
        item.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));//don't work on ContextMenu but keep is for the display text
        KeyCodeCombination keyCodeCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        tableView.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (keyCodeCombination.match(event)) {
                copyTableCell(tableView);
            }
        });

        MenuItem export = new MenuItem("Copy table (csv)",uniformDesign.createIcon(FontAwesome.Glyph.TABLE));
        export.setOnAction(event -> {
            exportTable(tableView);
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        menu.getItems().add(export);
        tableView.setContextMenu(menu);
        return tableView;
    }

    private void copyTableCell(final TableView<?> tableView) {
        StringBuilder clipboardString = new StringBuilder();
        for (TablePosition<?, ?> tablePosition : tableView.getSelectionModel().getSelectedCells()) {
            Object cell = tableView.getColumns().get(tablePosition.getColumn()).getCellData(tablePosition.getRow());
            clipboardString.append(cell);
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private String escapeCsvString(String s) {
        return "\"" +
                s.replace("\"", "\"\"") +
                "\"";
    }

    private void exportTable(final TableView<?> tableView) {
        StringBuilder clipboardString = new StringBuilder();

        for (TableColumn<?, ?> column : tableView.getColumns()) {
            clipboardString.append(escapeCsvString(column.getText())).append("\t");
        }
        clipboardString.append("\n");

        for (int i = 0; i < tableView.getItems().size(); i++) {
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                Object cellData = column.getCellData(i);
                String data = "";
                if (cellData != null) {
                    data = cellData.toString();
                }
                clipboardString.append(escapeCsvString(data)).append("\t");
            }
            clipboardString.append("\n");
        }

        final ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.PLAIN_TEXT, clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);

    }


}
