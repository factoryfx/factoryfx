package de.factoryfx.javafx.view;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.WidgetFactory;

public class ViewFactory<V> extends SimpleFactoryBase<View,V> {
    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>> viewDescription = new FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>>().setupUnsafe(ViewDescriptionFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V>> viewsDisplayWidget = new FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V>>().setupUnsafe(ViewsDisplayWidgetFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<Widget,WidgetFactory<V>> widget = new FactoryReferenceAttribute<Widget,WidgetFactory<V>>().setupUnsafe(WidgetFactory.class).de("view").en("view");

    @Override
    public View createImpl() {
        return new View(viewDescription.instance().text,viewsDisplayWidget.instance(),widget.instance());
    }
}
