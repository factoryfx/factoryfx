package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidgetFactory;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class ProductsViewFactory extends FactoryAwareWidgetFactory<JettyServerRootFactory> {

    public final FactoryAttribute<DataViewWidget<JettyServerRootFactory, Product,ProductFactory>, DataViewWidgetFactory<JettyServerRootFactory, Product,ProductFactory>> dataViewWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<JettyServerRootFactory> createWidget() {
        return new ProductsView(dataViewWidget.instance());
    }
}
