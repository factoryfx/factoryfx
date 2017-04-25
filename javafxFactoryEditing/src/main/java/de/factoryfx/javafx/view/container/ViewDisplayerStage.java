package de.factoryfx.javafx.view.container;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class ViewDisplayerStage implements ViewDisplayer {
    private final Stage stage;

    public ViewDisplayerStage(Stage stage) {
        this.stage = stage;
    }

    public void close(TabPane tabPane){
        stage.close();
    }

    public void show(TabPane tabPane){
        stage.show();
    }

    @Override
    public void focus(TabPane tabPane) {
        stage.toFront();
    }

}
