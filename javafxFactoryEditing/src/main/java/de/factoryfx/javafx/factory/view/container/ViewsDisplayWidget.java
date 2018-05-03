package de.factoryfx.javafx.factory.view.container;

import java.util.HashMap;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.view.View;
import de.factoryfx.javafx.data.widget.Widget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewsDisplayWidget implements Widget {
    private final TabPane tabPane;
    private final UniformDesign uniformDesign;
    private final LanguageText expandText = new LanguageText().en("expand").en("in neuem Fenster öffnen");

    public ViewsDisplayWidget(TabPane component, UniformDesign uniformDesign) {
        this.tabPane = component;
        this.uniformDesign = uniformDesign;
    }


    @Override
    public Node createContent() {
        return tabPane;
    }

    public Node createExpandReplacement(final Stage stage) {
        StackPane stackPane = new StackPane();
        Button button = new Button("edit expanded Window");
        button.setOnAction(event -> stage.toFront());
        stackPane.getChildren().add(button);
        return stackPane;
    }


    private HashMap<View,ViewDisplayer> viewToTab = new HashMap<>();

    public void close(View view) {
        ViewDisplayer viewDisplayer = viewToTab.get(view);
        if (viewDisplayer!=null){
            viewDisplayer.close(tabPane);
            viewToTab.remove(view);
        }
    }

    public void show(View view) {
        ViewDisplayer viewDisplayer = viewToTab.get(view);

        if (viewDisplayer == null) {
            final Tab tab = new Tab();
            viewDisplayer = new ViewDisplayerTab(tab);
            viewDisplayer.show(tabPane);
            viewToTab.put(view, viewDisplayer);

            ContextMenu contextMenu = new ContextMenu();
            MenuItem expand = new MenuItem(uniformDesign.getText(expandText));
            uniformDesign.addIcon(expand, FontAwesome.Glyph.EXPAND);
            expand.setOnAction(event -> {
                Stage stage = new Stage();
                stage.setTitle(tab.getText());
                Parent content = (Parent) tab.getContent();
                tab.setOnClosed(null);
                tab.setContent(new Region());
                tabPane.getTabs().remove(tab);
                tab.setContent(createExpandReplacement(stage));
                Scene scene = new Scene(content, 1380, 850);
                scene.getStylesheets().addAll(tabPane.getScene().getStylesheets());
                stage.setScene(scene);
                stage.show();
                stage.setOnCloseRequest(we -> {
                    close(view);
                });
                viewToTab.put(view, new ViewDisplayerStage(stage));
            });
            contextMenu.getItems().addAll(expand);
            tab.setContextMenu(contextMenu);

            tab.textProperty().bind(view.titleProperty());
            tab.setContent(view.createContent());

            tab.setOnClosed(event -> view.close());
        } else {
            viewDisplayer.focus(tabPane);
        }

    }

}