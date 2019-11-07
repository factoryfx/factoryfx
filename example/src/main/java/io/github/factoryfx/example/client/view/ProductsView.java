package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.example.server.shop.ShopResourceFactory;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import javafx.scene.Node;

public class ProductsView implements FactoryAwareWidget<JettyServerRootFactory> {

    private final DataViewWidget<JettyServerRootFactory, Product,ProductFactory> viewWidget;

    public ProductsView(DataViewWidget<JettyServerRootFactory, Product,ProductFactory> viewWidget) {
        this.viewWidget = viewWidget;
    }

    @Override
    public void edit(JettyServerRootFactory serverFactory) {
        viewWidget.edit(serverFactory.getResource(ShopResourceFactory.class).products);
    }

    @Override
    public Node createContent() {
        return viewWidget.createContent();
    }
}
