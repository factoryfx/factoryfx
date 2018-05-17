package de.factoryfx.javafx.factory.view.container;

import java.util.HashMap;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.view.View;
import de.factoryfx.javafx.data.widget.Widget;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
            final Tab tab = view.createTab();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem expand = new MenuItem(uniformDesign.getText(expandText));
            uniformDesign.addIcon(expand, FontAwesome.Glyph.EXPAND);
            expand.setOnAction(event -> {
                Stage stage =view.createStage();

                tabPane.getTabs().remove(tab);

                viewToTab.put(view, new ViewDisplayerStage(stage));
            });
            contextMenu.getItems().addAll(expand);
            tab.setContextMenu(contextMenu);


            viewDisplayer = new ViewDisplayerTab(tab);
            viewDisplayer.show(tabPane);
            viewToTab.put(view, viewDisplayer);
        } else {
            viewDisplayer.focus(tabPane);
        }

    }

}