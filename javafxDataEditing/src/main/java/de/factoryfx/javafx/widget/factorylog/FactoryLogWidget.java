package de.factoryfx.javafx.widget.factorylog;

import java.util.function.Consumer;

import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;

public class FactoryLogWidget implements Widget {
    private UniformDesign uniformDesign;

    public FactoryLogWidget(UniformDesign uniformDesign){
        this.uniformDesign=uniformDesign;
    }

    private Consumer<FactoryLogEntry> factoryLogEntryRootUpdater;
    FactoryLogEntry factoryLogEntry;

    public void updateLog(FactoryLogEntry factoryLogEntry) {
        this.factoryLogEntry=factoryLogEntry;
        if (factoryLogEntryRootUpdater!=null){
            factoryLogEntryRootUpdater.accept(factoryLogEntry);
        }
    }

    @Override
    public Node createContent() {
        final BorderPane borderPane = new BorderPane();
        factoryLogEntryRootUpdater= root -> {
            TreeView<FactoryLogWidgetTreeData> treeView = new TreeView<>();
            if (root!=null){
                treeView.setRoot(createLogTree(root));
            }
            treeView.setCellFactory(new Callback<TreeView<FactoryLogWidgetTreeData>, TreeCell<FactoryLogWidgetTreeData>>() {
                @Override
                public TreeCell<FactoryLogWidgetTreeData> call(TreeView<FactoryLogWidgetTreeData> param) {
                    return new TextFieldTreeCell<FactoryLogWidgetTreeData>(){
                        @Override
                        public void updateItem(FactoryLogWidgetTreeData item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty) {
                                setText(item.getText());
                                setGraphic(uniformDesign.createIcon(item.getIcon()));
                            }
                        }
                    };
                }
            });
            borderPane.setCenter(treeView);
        };
        factoryLogEntryRootUpdater.accept(factoryLogEntry);
        return borderPane;
    }

    private TreeItem<FactoryLogWidgetTreeData> createLogTree(FactoryLogEntry factoryLogEntry) {
        final TreeItem<FactoryLogWidgetTreeData> factoryLogEntryTreeItem = new TreeItem<>(new FactoryLogWidgetTreeDataFactory(factoryLogEntry));
        factoryLogEntryTreeItem.setExpanded(true);
        factoryLogEntry.events.forEach(event -> factoryLogEntryTreeItem.getChildren().add(new TreeItem<>(new FactoryLogWidgetTreeDataEvent(event))));
        factoryLogEntry.children.forEach(child -> factoryLogEntryTreeItem.getChildren().add(createLogTree(child)));
        return factoryLogEntryTreeItem;
    }

    public interface FactoryLogWidgetTreeData {
        String getText();
        FontAwesome.Glyph getIcon();
    }

    public static class FactoryLogWidgetTreeDataFactory implements FactoryLogWidgetTreeData {
        private final FactoryLogEntry factory;

        public FactoryLogWidgetTreeDataFactory(FactoryLogEntry factory) {
            this.factory = factory;
        }

        public String getText(){
            return factory.displayText;
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
            return event.type+" "+ (event.durationNs/ 1000000.0) +"ms";//(TimeUnit.NANOSECONDS.toMillis(event.durationNs));
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
            if (event.type== FactoryLogEntryEventType.REUSE){
                return FontAwesome.Glyph.EXCHANGE;
            }
            return null;
        }
    }
}
