package de.factoryfx.javafx.view.container;

import de.factoryfx.javafx.view.View;
import javafx.scene.layout.BorderPane;

public class BorderPaneViewContainer extends ViewContainer {
    private final BorderPane component;

    public BorderPaneViewContainer(BorderPane component) {
        this.component = component;
    }

    @Override
    protected void closeImpl(View view) {
        component.setCenter(null);
    }

    @Override
    public void showImpl(View view) {
        component.setCenter(view.createContent());
    }
}