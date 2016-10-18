package de.factoryfx.javafx.stage;

import java.util.List;

import com.google.common.base.Strings;
import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BorderPaneStage {

    public BorderPaneStage(Stage stage, List<Menu> menus, ViewsDisplayWidget instance, int width, int height, StackPane stackPane, String cssResourceUrlExternalForm) {



        BorderPane root = new BorderPane();
        root.setCenter(instance.createContent());
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menus);
        root.setTop(menuBar);

        stackPane.getChildren().add(root);

        if (!Strings.isNullOrEmpty(cssResourceUrlExternalForm)){
            root.getStylesheets().add(cssResourceUrlExternalForm);
        }

        stage.setScene(new Scene(stackPane,width,height));


    }


}
