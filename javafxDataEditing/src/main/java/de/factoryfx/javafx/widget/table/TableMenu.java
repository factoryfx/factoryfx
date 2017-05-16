package de.factoryfx.javafx.widget.table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.factoryfx.javafx.util.UniformDesign;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.controlsfx.glyphfont.FontAwesome;

public class TableMenu<T> {
    private final UniformDesign uniformDesign;

    public TableMenu(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    public TableView<T> addMenu(TableView<T> tableView){
        tableView.setTableMenuButtonVisible(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addMenu(tableView,()->copyTableCell(tableView), ()->createCsvFromTable(tableView));
        return tableView;
    }

    public TreeTableView<T> addMenu(TreeTableView<T> treeTableView){
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addMenu(treeTableView,()->copyTreeTableCell(treeTableView), ()->createCsvFromTreeTable(treeTableView));
        return treeTableView;
    }

    public void addMenu(Control tableView, Runnable copyTableCell, Supplier<String> createCsvFromTable){

        MenuItem item = new MenuItem("Copy cell",uniformDesign.createIcon(FontAwesome.Glyph.COPY));
        item.setOnAction(event -> {
            copyTableCell.run();
        });
        item.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));//don't work on ContextMenu but keep is for the display text
        KeyCodeCombination keyCodeCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        tableView.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (keyCodeCombination.match(event)) {
                copyTableCell.run();
            }
        });

        MenuItem export = new MenuItem("Copy table (csv)",uniformDesign.createIcon(FontAwesome.Glyph.TABLE));
        export.setOnAction(event -> {
            exportTableToClipboard(createCsvFromTable.get());
        });

        MenuItem fileExport = new MenuItem("Save table (csv)",uniformDesign.createIcon(FontAwesome.Glyph.FILE));
        fileExport.setOnAction(event -> {
            exportTableToFile(createCsvFromTable.get(),tableView.getScene().getWindow());
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        menu.getItems().add(export);
        menu.getItems().add(fileExport);
        tableView.setContextMenu(menu);
    }

    private void copyTreeTableCell(final TreeTableView<T> tableView) {
        StringBuilder clipboardString = new StringBuilder();
        for (TreeTablePosition<?, ?> tablePosition : tableView.getSelectionModel().getSelectedCells()) {
            Object cell = tableView.getColumns().get(tablePosition.getColumn()).getCellData(tablePosition.getRow());
            clipboardString.append(cell);
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);
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

    private void exportTableToClipboard(String csvString) {
        final ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.PLAIN_TEXT, csvString);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void exportTableToFile(String csvString, Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(ownerWindow);
        if(file != null){
            try {
                Files.write( file.toPath(), csvString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String createCsvFromTable(TableView<T> tableView) {
        StringBuilder result = new StringBuilder();

        for (TableColumn<?, ?> column : tableView.getColumns()) {
            result.append(escapeCsvString(column.getText())).append("\t");
        }
        result.append("\n");

        for (int i = 0; i < tableView.getItems().size(); i++) {
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                Object cellData = column.getCellData(i);
                String data = "";
                if (cellData != null) {
                    data = cellData.toString();
                }
                result.append(escapeCsvString(data)).append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private String createCsvFromTreeTable(TreeTableView<T> tableView) {
        StringBuilder result = new StringBuilder();

        for (TreeTableColumn<?, ?> column : tableView.getColumns()) {
            result.append(escapeCsvString(column.getText())).append("\t");
        }
        result.append("\n");

        ArrayList<T> items = new ArrayList<>();
        collectTreeTableItems(tableView.getRoot(), items);

        for (int i = 0; i < items.size(); i++) {
            for (TreeTableColumn<?, ?> column : tableView.getColumns()) {
                Object cellData = column.getCellData(i);
                String data = "";
                if (cellData != null) {
                    data = cellData.toString();
                }
                result.append(escapeCsvString(data)).append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void collectTreeTableItems(TreeItem<T> item, List<T> list){
        list.add(item.getValue());
        item.getChildren().forEach(t -> collectTreeTableItems(t,list));
    }

}
