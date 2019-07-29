package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidgetFactory;

public class ProductsViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {

    public final FactoryAttribute<DataViewWidget<ServerRootFactory, Product,ProductFactory>, DataViewWidgetFactory<ServerRootFactory, Product,ProductFactory>> dataViewWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ProductsView(dataViewWidget.instance());
    }
}
