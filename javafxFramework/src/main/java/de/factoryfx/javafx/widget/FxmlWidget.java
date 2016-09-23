package de.factoryfx.javafx.widget;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class FxmlWidget<C extends FxmlController> implements Widget {
    private final C controller;

    protected Node content;

    public FxmlWidget(C controller) {
        this.controller = controller;
    }

    @Override
    public Node createContent() {
        if (content == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(controller.getFxmlResource());
            fxmlLoader.setController(controller);

            try {
                content = (Parent) fxmlLoader.load();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        return content;
    }

    public C getController() {
        return controller;
    }
}
