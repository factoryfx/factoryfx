package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.shop.ProductFactory;
import io.github.factoryfx.example.server.shop.ShopResourceFactory;
import io.github.factoryfx.javafx.data.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import javafx.scene.Node;

public class ProductsView implements FactoryAwareWidget<ServerRootFactory> {

    private final DataViewWidget<ProductFactory> viewWidget;

    public ProductsView(DataViewWidget<ProductFactory> viewWidget) {
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
