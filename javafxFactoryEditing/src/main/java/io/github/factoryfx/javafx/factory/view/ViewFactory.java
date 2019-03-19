package io.github.factoryfx.javafx.factory.view;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidget;
import io.github.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;

public class ViewFactory extends SimpleFactoryBase<View,RichClientRoot> {
    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryReferenceAttribute<>(ViewDescriptionFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<ViewsDisplayWidget, ViewsDisplayWidgetFactory> viewsDisplayWidget = new FactoryReferenceAttribute<>(ViewsDisplayWidgetFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<Widget, WidgetFactory> widget = new FactoryReferenceAttribute<>(WidgetFactory.class).de("view").en("view");

    @Override
    public View createImpl() {
        return new View(viewDescription.instance(),viewsDisplayWidget.instance(),widget.instance());
    }
}
