package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.dataview.DataViewWidgetFactory;

public class ProductsViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {

    public final FactoryAttribute<RichClientRoot, DataViewWidget<ServerRootFactory, Product,ProductFactory>, DataViewWidgetFactory<ServerRootFactory, Product,ProductFactory>> dataViewWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ProductsView(dataViewWidget.instance());
    }
}
