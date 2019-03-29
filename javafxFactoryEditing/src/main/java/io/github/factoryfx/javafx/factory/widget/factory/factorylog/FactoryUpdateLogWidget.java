package io.github.factoryfx.javafx.factory.widget.factory.factorylog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.table.TableControlWidget;
import io.github.factoryfx.factory.log.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import org.controlsfx.glyphfont.FontAwesome;

public class FactoryUpdateLogWidget implements Widget {
    private UniformDesign uniformDesign;

    public FactoryUpdateLogWidget(UniformDesign uniformDesign){
        this.uniformDesign=uniformDesign;
    }

    private Consumer<FactoryUpdateLog> factoryLogRootUpdater;
    FactoryUpdateLog<?> factoryLog;

    public void updateLog(FactoryUpdateLog factoryLog) {
        this.factoryLog=factoryLog;
        if (factoryLogRootUpdater!=null){
            factoryLogRootUpdater.accept(factoryLog);
        }
    }

    @Override
    public Node createContent() {
        final BorderPane borderPane = new BorderPane();
        factoryLogRootUpdater= root -> {
            TreeView<FactoryLogWidgetTreeData> treeView = new TreeView<>();
            if (factoryLog.root!=null){
                treeView.setRoot(createLogTree(factoryLog.root, System.currentTimeMillis()+5000,new HashMap<>()));
            }
            treeView.setCellFactory(param-> new TextFieldTreeCell<>(){
                @Override
                public void updateItem(FactoryLogWidgetTreeData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(item.getText());
                        setGraphic(uniformDesign.createIcon(item.getIcon()));
                    }
                }
            });
            final TabPane tabPane = new TabPane();
            tabPane.getStyleClass().add("floating");

            final Tab treeTab = new Tab("Updated Tree");
            treeTab.setContent(treeView);
            tabPane.getTabs().add(treeTab);

            final Tab tableTab = new Tab("Updated Table");
            tableTab.setContent(treeView);
            if (factoryLog.root!=null){
                tableTab.setContent(createTable(factoryLog.root.getListDeep()));
            }
            tabPane.getTabs().add(tableTab);

            final Tab removedTab = new Tab("Removed");
            removedTab.setContent(treeView);
            removedTab.setContent(createTable(factoryLog.removedFactoryLogs));
            tabPane.getTabs().add(removedTab);

            borderPane.setCenter(tabPane);
            final Label totalDuarion = new Label("total edit duration: " + (factoryLog.totalDurationNs / 1000000.0) + "ms");
            BorderPane.setMargin(totalDuarion,new Insets(3));
            borderPane.setTop(totalDuarion);
        };
        if (factoryLog!=null) {
            factoryLogRootUpdater.accept(factoryLog);
        }
        return borderPane;
    }

    private Node createTable(Set<FactoryLogEntry>  items){
        items.removeIf(Objects::isNull);
        final TableView<FactoryLogEntry> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getItems().addAll(items);
        final TableColumn<FactoryLogEntry, String> name = new TableColumn<>("Factory");
        name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFactoryDescription()));
        tableView.getColumns().add(name);

        for (FactoryLogEntryEventType type: FactoryLogEntryEventType.values()){
            final TableColumn<FactoryLogEntry, String> typ = new TableColumn<>(type.toString());
            typ.setCellValueFactory(param -> new SimpleStringProperty(getTypeText(param.getValue(),type)));
            tableView.getColumns().add(typ);
        }
        TableControlWidget<FactoryLogEntry> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setBottom(tableControlWidget.createContent());
        return borderPane;
    }

    private String getTypeText(FactoryLogEntry entry, FactoryLogEntryEventType type){
        for (FactoryLogEntryEvent event: entry.getEvents()){
            if (type==event.type){
                return( event.durationNs / 1000000.0) + "ms";
            }
        }
        return null;
    }

    private TreeItem<FactoryLogWidgetTreeData> createLogTree(FactoryLogEntryTreeItem factoryLogEntry, long abortOnCurrentTimeMillis, Map<FactoryLogEntryTreeItem, TreeItem<FactoryLogWidgetTreeData>> createdTreeItems) {
        if (createdTreeItems.containsKey(factoryLogEntry)){
            return createdTreeItems.get(factoryLogEntry);
        }
        final TreeItem<FactoryLogWidgetTreeData> factoryLogEntryTreeItem = new TreeItem<>(new FactoryLogWidgetTreeDataFactory(factoryLogEntry));
        createdTreeItems.put(factoryLogEntry,factoryLogEntryTreeItem);
        factoryLogEntryTreeItem.setExpanded(true);
        if (System.currentTimeMillis() < abortOnCurrentTimeMillis) {
            factoryLogEntry.log.getEvents().forEach(event -> factoryLogEntryTreeItem.getChildren().add(new TreeItem<>(new FactoryLogWidgetTreeDataEvent(event))));
            factoryLogEntry.children.forEach(child -> factoryLogEntryTreeItem.getChildren().add(createLogTree(child, abortOnCurrentTimeMillis,createdTreeItems)));
        }
        return factoryLogEntryTreeItem;
    }

    public interface FactoryLogWidgetTreeData {
        String getText();
        FontAwesome.Glyph getIcon();
    }

    public static class FactoryLogWidgetTreeDataFactory implements FactoryLogWidgetTreeData {
        private final FactoryLogEntryTreeItem factory;

        public FactoryLogWidgetTreeDataFactory(FactoryLogEntryTreeItem factory) {
            this.factory = factory;
        }

        public String getText(){
            return factory.log.getFactoryDescription();
        }

        public FontAwesome.Glyph getIcon(){
            return FontAwesome.Glyph.SQUARE_ALT;
        }
    }

    public static class FactoryLogWidgetTreeDataEvent implements FactoryLogWidgetTreeData {
        private final FactoryLogEntryEvent event;

        public FactoryLogWidgetTreeDataEvent(FactoryLogEntryEvent event) {
            this.event = event;
        }

        public String getText(){
            return event.type+" "+ (event.durationNs/ 1000000.0) +" ms";//(TimeUnit.NANOSECONDS.toMillis(event.durationNs));
        }

        public FontAwesome.Glyph getIcon(){
            if (event.type== FactoryLogEntryEventType.START){
                return FontAwesome.Glyph.PLAY_CIRCLE;
            }
            if (event.type== FactoryLogEntryEventType.CREATE){
                return FontAwesome.Glyph.PLUS_CIRCLE;
            }
            if (event.type== FactoryLogEntryEventType.DESTROY){
                return FontAwesome.Glyph.MINUS;
            }
            if (event.type== FactoryLogEntryEventType.RECREATE){
                return FontAwesome.Glyph.REFRESH;
            }
            return null;
        }
    }
}
