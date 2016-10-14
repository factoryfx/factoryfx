package de.factoryfx.javafx;

import de.factoryfx.javafx.stage.StageFactory;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.view.ViewDescriptionFactory;
import de.factoryfx.javafx.view.ViewFactory;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.view.menu.ViewMenuFactory;
import de.factoryfx.javafx.view.menu.ViewMenuItemFactory;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.WidgetFactory;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class IntegrationTest extends Application{

    public static void main(String[] args) {
        Application.launch();
    }

    public class ViewXWidgetFactory extends WidgetFactory<Void>{

        @Override
        protected Widget createWidget() {
            return () -> {
                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(new Label("Hello World"));
                return borderPane;
            };
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        UniformDesignFactory<Void> uniformDesignFactory = new UniformDesignFactory<>();

        ViewsDisplayWidgetFactory<Void> viewsDisplayWidgetFactory = new ViewsDisplayWidgetFactory<>();
        viewsDisplayWidgetFactory.uniformDesign.set(uniformDesignFactory);

        ViewDescriptionFactory<Void> viewDescriptionFactory = new ViewDescriptionFactory<>();
        viewDescriptionFactory.text.en("Config").de("Config");
        viewDescriptionFactory.uniformDesign.set(uniformDesignFactory);

        ViewFactory<Void> viewFactory = new ViewFactory<>();
        viewFactory.viewDescription.set(viewDescriptionFactory);
        viewFactory.viewsDisplayWidget.set(viewsDisplayWidgetFactory);
        viewFactory.widget.set(new ViewXWidgetFactory());

        ViewMenuItemFactory<Void> viewMenuItemFactory = new ViewMenuItemFactory<>();
        viewMenuItemFactory.uniformDesign.set(uniformDesignFactory);
        viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
        viewMenuItemFactory.view.set(viewFactory);

        ViewMenuFactory<Void> menuFactory = new ViewMenuFactory<>();
        menuFactory.text.en("File").de("Datei");
        menuFactory.items.add(viewMenuItemFactory);
        menuFactory.uniformDesign.set(uniformDesignFactory);

        StageFactory<Void> stageFactory = new StageFactory<>();
        stageFactory.stage.set(primaryStage);
        stageFactory.items.add(menuFactory);
        stageFactory.width.set(1920);
        stageFactory.height.set(1080);
        stageFactory.viewsDisplayWidget.set(viewsDisplayWidgetFactory);

        stageFactory.instance();
        stageFactory.start();
    }
}
