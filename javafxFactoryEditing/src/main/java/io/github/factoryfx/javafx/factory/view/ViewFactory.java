package io.github.factoryfx.javafx.factory.view;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidget;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;

public class ViewFactory extends SimpleFactoryBase<View,RichClientRoot> {
    public final FactoryAttribute<RichClientRoot,ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot,ViewsDisplayWidget, ViewsDisplayWidgetFactory> viewsDisplayWidget = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot,Widget, WidgetFactory> widget = new FactoryAttribute<>();

    @Override
    public View createImpl() {
        return new View(viewDescription.instance(),viewsDisplayWidget.instance(),widget.instance());
    }
}
