package de.factoryfx.javafx.stage;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.util.LongRunningActionExecutor;
import de.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.view.menu.ViewMenuFactory;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

public class StageFactory<V> extends FactoryBase<BorderPaneStage,V> {
    public final ObjectValueAttribute<Stage> stage = new ObjectValueAttribute<>(new AttributeMetadata().en("main stage"));
    public final FactoryReferenceListAttribute<Menu,ViewMenuFactory<V>> items = new FactoryReferenceListAttribute<>(new AttributeMetadata().de("items").en("items"),ViewMenuFactory.class);
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V>> viewsDisplayWidget = new FactoryReferenceAttribute<>(new AttributeMetadata().de("items").en("items"),ViewsDisplayWidgetFactory.class);
    public final IntegerAttribute width = new IntegerAttribute(new AttributeMetadata().de("width").en("width"));
    public final IntegerAttribute height = new IntegerAttribute(new AttributeMetadata().de("height").en("height"));
    public final FactoryReferenceAttribute<LongRunningActionExecutor,LongRunningActionExecutorFactory<V>> longRunningActionExecutor = new FactoryReferenceAttribute<>(new AttributeMetadata().de("items").en("items"),LongRunningActionExecutorFactory.class);
    public final StringAttribute cssResourceUrlExternalForm = new StringAttribute(new AttributeMetadata().de("cssResourceUrlExternalForm").en("cssResourceUrlExternalForm"));

    @Override
    public LiveCycleController<BorderPaneStage, V> createLifecycleController() {
        return new LiveCycleController<BorderPaneStage, V>() {
            @Override
            public BorderPaneStage create() {
                return new BorderPaneStage(stage.get(),items.instances(),viewsDisplayWidget.instance(),width.get(),height.get(), longRunningActionExecutor.instance().getStackPane(),cssResourceUrlExternalForm.get());
            }

            @Override
            public void start(BorderPaneStage newLiveObject) {
                stage.get().show();
            }

            @Override
            public void destroy(BorderPaneStage previousLiveObject) {
                stage.get().hide();
            };
        };
    }
}
