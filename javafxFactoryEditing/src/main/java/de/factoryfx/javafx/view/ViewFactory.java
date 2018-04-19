package de.factoryfx.javafx.view;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.WidgetFactory;

public class ViewFactory<V, R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<View,V,R> {
    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V,R>> viewDescription = new FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V,R>>().setupUnsafe(ViewDescriptionFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V,R>> viewsDisplayWidget = new FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V,R>>().setupUnsafe(ViewsDisplayWidgetFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<Widget,WidgetFactory<V,R>> widget = new FactoryReferenceAttribute<Widget,WidgetFactory<V,R>>().setupUnsafe(WidgetFactory.class).de("view").en("view");

    @Override
    public View createImpl() {
        return new View(viewDescription.instance().text,viewsDisplayWidget.instance(),widget.instance());
    }
}
