package io.github.factoryfx.javafx.widget;

import javafx.scene.Node;

public interface Widget {
    Node createContent();
    default void destroy(){

    }
}
