package io.github.factoryfx.javafx.view;

import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.view.container.ViewsDisplayWidget;
import io.github.factoryfx.javafx.widget.Widget;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class View implements Widget {

    private final ViewDescription viewDescription;
    private final Widget viewContent;
    private final ViewsDisplayWidget viewsDisplayWidget;


    public View(ViewDescription viewDescription, ViewsDisplayWidget viewsDisplayWidget, Widget viewContent) {
        this.viewDescription = viewDescription;
        this.viewsDisplayWidget = viewsDisplayWidget;
        this.viewContent = viewContent;
    }

    public void close() {
        viewsDisplayWidget.close(this);
        viewContent.destroy();
    }

    @Override
    public Parent createContent() {
        final ScrollPane scrollPane = new ScrollPane(viewContent.createContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    public void show() {
        viewsDisplayWidget.show(this);
    }

    public Tab createTab(){
        final Tab tab = new Tab();
        viewDescription.describeTabView(tab);

        tab.setContent(createContent());
        tab.setOnClosed(event -> close());
        return tab;
    }


    public Stage createStage() {
        Stage stage = new Stage();
        viewDescription.describeStageView(stage);

        Scene scene = new Scene(createContent(), 1380, 850);
        CssUtil.addToScene(scene);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(we -> {
            close();
        });

        return stage;
    }
}
