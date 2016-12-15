package de.factoryfx.javafx.view;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.WidgetFactory;

public class ViewFactory<V> extends SimpleFactoryBase<View,V> {
    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>> viewDescription = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),ViewDescriptionFactory.class);
    public final FactoryReferenceAttribute<ViewsDisplayWidget,ViewsDisplayWidgetFactory<V>> viewsDisplayWidget = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),ViewsDisplayWidgetFactory.class);
    public final FactoryReferenceAttribute<Widget,WidgetFactory<V>> widget = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),WidgetFactory.class);

    @Override
    public View createImpl() {
        return new View(viewDescription.instance().text,viewsDisplayWidget.instance(),widget.instance());
    }
}
