package de.factoryfx.javafx.view.container;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.view.View;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class ViewsDisplayWidget implements Widget {
    private final TabPane tabpane;
    private final UniformDesign uniformDesign;
    private List<Stage> stages = new ArrayList<>();

    public ViewsDisplayWidget(TabPane component, UniformDesign uniformDesign) {
        this.tabpane = component;
        this.uniformDesign = uniformDesign;
    }


    @Override
    public Node createContent() {
        return tabpane;
    }

    public Node createExpandReplacement(final Stage stage) {
        StackPane stackPane = new StackPane();
        Button button = new Button("edit expanded Window");
        button.setOnAction(event -> stage.toFront());
        stackPane.getChildren().add(button);
        return stackPane;
    }


    protected CloseListener onCloseListener;

    public void close(View view) {
        Tab formTab = null;
        for (Tab tab : tabpane.getTabs()) {
            if (tab.getContent() == view.getCachedContent()) {
                formTab = tab;
            }
        }
        if (formTab != null) {
            tabpane.getTabs().remove(formTab);
        }

        if (onCloseListener != null) {
            onCloseListener.closed(view);
        }
    }

    /**
     * called if view is closed
     */
    public void setOnCloseListener(CloseListener closeListener) {
        onCloseListener = closeListener;
    }

    public void show(View view) {
        Tab formTab = null;
        for (Tab tab : tabpane.getTabs()) {
            if (tab.getContent() == view.getCachedContent()) {
                formTab = tab;
            }
        }

        boolean existingExpanded = false;
        for (Stage stage : stages) {
            if (stage.getScene().getRoot() == view.getCachedContent()) {
                stage.toFront();
                existingExpanded = true;
            }
        }

        if (formTab == null) {
            formTab = new Tab();
            if (!existingExpanded) {
                tabpane.getTabs().add(formTab);
            }
        }
        final Tab formTabFinal = formTab;

        final EventHandler<Event> eventEventHandler = event -> {
            formTabFinal.setContent(null);
            formTabFinal.textProperty().unbind();
            formTabFinal.setOnClosed(null);
            view.close();
        };

        ContextMenu contextMenu = new ContextMenu();
        MenuItem expand = new MenuItem("expand");
        expand.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle(formTabFinal.getText());
            Parent content = (Parent) formTabFinal.getContent();
            formTabFinal.setOnClosed(null);
            formTabFinal.setContent(new Region());
            tabpane.getTabs().remove(formTabFinal);
            formTabFinal.setContent(createExpandReplacement(stage));
            Scene scene = new Scene(content, 1380, 850);
            scene.getStylesheets().addAll(tabpane.getScene().getStylesheets());
            stages.add(stage);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(we -> {
                Parent content1 = stage.getScene().getRoot();
                stage.getScene().setRoot(new Pane());
                formTabFinal.setContent(content1);
                formTabFinal.setOnClosed(eventEventHandler);
                stages.remove(stage);
            });
            formTabFinal.setOnClosed(event1 -> {
                stage.close();
                eventEventHandler.handle(event1);
            });

        });
        uniformDesign.addIcon(expand, FontAwesome.Glyph.EXPAND);

        contextMenu.getItems().addAll(expand);
        formTab.setContextMenu(contextMenu);

        formTab.textProperty().bind(view.title);
        formTab.setContent(view.getCachedContent());
        ChangeListener<Glyph> listener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                formTabFinal.setGraphic(newValue);
            }
        };
        view.icon.addListener(listener);
        listener.changed(view.icon, view.icon.get(), view.icon.get());

        tabpane.getSelectionModel().select(formTab);

        formTab.setOnClosed(eventEventHandler);

    }

    public interface CloseListener {
        void closed(View view);
    }

}