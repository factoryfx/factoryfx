package de.factoryfx.javafx.factory.view;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;

public class ViewFactory extends SimpleFactoryBase<View,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryReferenceAttribute<>(ViewDescriptionFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory> viewsDisplayWidget = new FactoryReferenceAttribute<>(ViewsDisplayWidgetFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<Widget,WidgetFactory> widget = new FactoryReferenceAttribute<>(WidgetFactory.class).de("view").en("view");

    @Override
    public View createImpl() {
        return new View(viewDescription.instance(),viewsDisplayWidget.instance(),widget.instance());
    }
}
