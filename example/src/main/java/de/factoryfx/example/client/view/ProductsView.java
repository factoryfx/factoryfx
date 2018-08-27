package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.ProductFactory;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.dataview.DataView;
import de.factoryfx.javafx.data.widget.dataview.DataViewWidget;
import de.factoryfx.javafx.data.widget.dataview.ReferenceAttributeDataView;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import javafx.scene.Node;
import javafx.scene.control.TableView;

public class ProductsView implements FactoryAwareWidget<ServerRootFactory> {

    private final DataViewWidget<ProductFactory> viewWidget;

    public ProductsView(DataViewWidget<ProductFactory> viewWidget) {
        this.viewWidget = viewWidget;
    }

    @Override
    public void edit(ServerRootFactory serverFactory) {
        viewWidget.edit(serverFactory.httpServer.get().shopResource.get().products);
    }

    @Override
    public Node createContent() {
        return viewWidget.createContent();
    }
}
