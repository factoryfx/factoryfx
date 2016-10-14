package de.factoryfx.javafx.view.container;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory<V> extends FactoryBase<ViewsDisplayWidget,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),UniformDesignFactory.class);

    @Override
    public LiveCycleController<ViewsDisplayWidget, V> createLifecycleController() {
        return () -> new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}