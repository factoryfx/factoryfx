package io.github.factoryfx.javafx;

import io.github.factoryfx.javafx.factory.stage.StageFactory;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import io.github.factoryfx.javafx.factory.view.ViewDescriptionFactory;
import io.github.factoryfx.javafx.factory.view.ViewFactory;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.factory.view.menu.ViewMenuFactory;
import io.github.factoryfx.javafx.factory.view.menu.ViewMenuItemFactory;
import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class IntegrationTest extends Application{

    public static void main(String[] args) {
        Application.launch();
    }

    public class ViewXWidgetFactory extends WidgetFactory {

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
        LongRunningActionExecutorFactory longRunningActionExecutorFactory = new  LongRunningActionExecutorFactory();

        UniformDesignFactory uniformDesignFactory = new UniformDesignFactory();

        ViewsDisplayWidgetFactory viewsDisplayWidgetFactory = new ViewsDisplayWidgetFactory();
        viewsDisplayWidgetFactory.uniformDesign.set(uniformDesignFactory);

        ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
        viewDescriptionFactory.text.en("Config").de("Config");
        viewDescriptionFactory.uniformDesign.set(uniformDesignFactory);

        ViewFactory viewFactory = new ViewFactory();
        viewFactory.viewDescription.set(viewDescriptionFactory);
        viewFactory.viewsDisplayWidget.set(viewsDisplayWidgetFactory);
        viewFactory.widget.set(new ViewXWidgetFactory());

        ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
        viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
        viewMenuItemFactory.view.set(viewFactory);

        ViewMenuFactory menuFactory = new ViewMenuFactory();
        menuFactory.text.en("File").de("Datei");
        menuFactory.items.add(viewMenuItemFactory);
        menuFactory.uniformDesign.set(uniformDesignFactory);

        StageFactory stageFactory = new StageFactory();
        stageFactory.stage.set(primaryStage);
        stageFactory.items.add(menuFactory);
        stageFactory.width.set(1920);
        stageFactory.height.set(1080);
        stageFactory.viewsDisplayWidget.set(viewsDisplayWidgetFactory);
        stageFactory.longRunningActionExecutor.set(longRunningActionExecutorFactory);

        stageFactory.internalFactory().instance();
        stageFactory.internalFactory().start();
    }
}
