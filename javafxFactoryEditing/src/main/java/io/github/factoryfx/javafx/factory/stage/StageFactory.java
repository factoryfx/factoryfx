package io.github.factoryfx.javafx.factory.stage;

import io.github.factoryfx.data.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.data.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.data.attribute.types.StringListAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidget;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.factory.view.menu.ViewMenuFactory;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 */
public class StageFactory extends FactoryBase<Stage,RichClientRoot> {
    public final ObjectValueAttribute<Stage> stage = new ObjectValueAttribute<Stage>().en("main stage");
    public final FactoryReferenceListAttribute<Menu,ViewMenuFactory> items = new FactoryReferenceListAttribute<>(ViewMenuFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory> viewsDisplayWidget =new FactoryReferenceAttribute<>(ViewsDisplayWidgetFactory.class).de("viewsDisplayWidget").en("viewsDisplayWidget");
    public final IntegerAttribute width = new IntegerAttribute().de("width").en("width");
    public final IntegerAttribute height = new IntegerAttribute().de("height").en("height");
    public final FactoryReferenceAttribute<LongRunningActionExecutor,LongRunningActionExecutorFactory> longRunningActionExecutor =new FactoryReferenceAttribute<>(LongRunningActionExecutorFactory.class).de("longRunningActionExecutor").en("longRunningActionExecutor");
    public final StringListAttribute cssResourceUrlExternalForm = new StringListAttribute().de("cssResourceUrlExternalForm").en("cssResourceUrlExternalForm");
    public final StringAttribute title = new StringAttribute().de("title").en("title").nullable();

    public StageFactory(){
        cssResourceUrlExternalForm.add(CssUtil.getURL());

        configLifeCycle().setCreator(this::setupStage);
        configLifeCycle().setStarter((newLiveObject) -> stage.get().show());
        configLifeCycle().setDestroyer((previousLiveObject) -> stage.get().hide());
    }

    private Stage setupStage() {
        Stage stage = this.stage.get();

        BorderPane root = new BorderPane();
        root.setCenter(viewsDisplayWidget.instance().createContent());
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(items.instances());
        root.setTop(menuBar);

        LongRunningActionExecutor longRunningActionExecutor = this.longRunningActionExecutor.instance();


        for (String cssUrl: cssResourceUrlExternalForm){
            root.getStylesheets().add(cssUrl);
        }

        stage.setScene(new Scene(longRunningActionExecutor.wrap(root),width.get(),height.get()));
        stage.setTitle(title.get());


        return stage;
    }
}
