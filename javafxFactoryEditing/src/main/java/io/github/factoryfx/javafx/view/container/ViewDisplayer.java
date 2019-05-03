package io.github.factoryfx.javafx.view.container;

import javafx.scene.control.TabPane;

public interface ViewDisplayer {
    void close(TabPane tabPane);
    void show(TabPane tabPane);
    void focus(TabPane tabPane);
}
