package de.factoryfx.javafx.factory.stage;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.stage.BorderPaneStage;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.factory.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.factory.view.menu.ViewMenuFactory;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

/**
 */
public class StageFactory extends FactoryBase<BorderPaneStage,Void,RichClientRoot> {
    public final ObjectValueAttribute<Stage> stage = new ObjectValueAttribute<Stage>().en("main stage");
    public final FactoryReferenceListAttribute<Menu,ViewMenuFactory> items = new FactoryReferenceListAttribute<Menu,ViewMenuFactory>().de("items").en("items");
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory> viewsDisplayWidget = new FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory>().de("items").en("items");
    public final IntegerAttribute width = new IntegerAttribute().de("width").en("width");
    public final IntegerAttribute height = new IntegerAttribute().de("height").en("height");
    public final FactoryReferenceAttribute<LongRunningActionExecutor,LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryReferenceAttribute<LongRunningActionExecutor,LongRunningActionExecutorFactory>().de("items").en("items");
    public final StringListAttribute cssResourceUrlExternalForm = new StringListAttribute().de("cssResourceUrlExternalForm").en("cssResourceUrlExternalForm");

    public StageFactory(){
        cssResourceUrlExternalForm.add(CssUtil.getURL());

        configLiveCycle().setCreator(() -> {
            return new BorderPaneStage(stage.get(),items.instances(),viewsDisplayWidget.instance(),width.get(),height.get(), longRunningActionExecutor.instance().getStackPane(),cssResourceUrlExternalForm.get());
        });
        configLiveCycle().setStarter((newLiveObject) -> {
            stage.get().show();
        });
        configLiveCycle().setDestroyer((previousLiveObject) -> {
            stage.get().hide();
        });
    }
}
