package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.dataview.DataViewWidgetFactory;

public class ProductsViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<DataViewWidget, DataViewWidgetFactory<ProductFactory>> dataViewWidget =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(DataViewWidgetFactory.class));

    @SuppressWarnings("unchecked")
    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ProductsView(dataViewWidget.instance());
    }
}
