package de.factoryfx.javafx.factory.view.container;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ViewDisplayerTab implements ViewDisplayer {
    private final Tab tab;

    public ViewDisplayerTab(Tab tab) {
        this.tab = tab;
    }

    public void close(TabPane tabPane){
        tab.setContent(null);
        tab.textProperty().unbind();
        tab.setOnClosed(null);
        tabPane.getTabs().remove(tab);
    }

    public void show(TabPane tabPane){
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    @Override
    public void focus(TabPane tabPane) {
        tabPane.getSelectionModel().select(tab);
    }

}
