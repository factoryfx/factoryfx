package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.example.server.shop.ShopResourceFactory;
import io.github.factoryfx.javafx.factory.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidget;
import javafx.scene.Node;

public class ProductsView implements FactoryAwareWidget<ServerRootFactory> {

    private final DataViewWidget<ServerRootFactory, Product,ProductFactory> viewWidget;

    public ProductsView(DataViewWidget<ServerRootFactory, Product,ProductFactory> viewWidget) {
        this.viewWidget = viewWidget;
    }

    @Override
    public void edit(ServerRootFactory serverFactory) {
        viewWidget.edit(serverFactory.httpServer.get().getResource(ShopResourceFactory.class).products);
    }

    @Override
    public Node createContent() {
        return viewWidget.createContent();
    }
}
