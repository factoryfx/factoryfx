package de.factoryfx.javafx.widget;

import javafx.scene.Node;

public interface CloseAwareWidget {
    void closeNotifier();
    Node createContent();
}
