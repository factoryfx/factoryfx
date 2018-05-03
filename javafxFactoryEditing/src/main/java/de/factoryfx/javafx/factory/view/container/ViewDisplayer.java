package de.factoryfx.javafx.factory.view.container;

import javafx.scene.control.TabPane;

public interface ViewDisplayer {
    void close(TabPane tabPane);
    void show(TabPane tabPane);
    void focus(TabPane tabPane);
}
