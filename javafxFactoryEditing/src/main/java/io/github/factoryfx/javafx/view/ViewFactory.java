package io.github.factoryfx.javafx.view;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.view.container.ViewsDisplayWidget;
import io.github.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.factory.WidgetFactory;

public class ViewFactory extends SimpleFactoryBase<View,RichClientRoot> {
    public final FactoryAttribute<ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryAttribute<>();
    public final FactoryAttribute<ViewsDisplayWidget, ViewsDisplayWidgetFactory> viewsDisplayWidget = new FactoryAttribute<>();
    public final FactoryAttribute<Widget, WidgetFactory> widget = new FactoryAttribute<>();

    @Override
    protected View createImpl() {
        return new View(viewDescription.instance(),viewsDisplayWidget.instance(),widget.instance());
    }
}
