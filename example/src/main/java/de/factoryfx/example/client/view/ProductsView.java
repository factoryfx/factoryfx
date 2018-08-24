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

    private final DataEditor dataEditor;
    private final UniformDesign uniformDesign;

    public ProductsView(DataEditor dataEditor, UniformDesign uniformDesign) {
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node init(ServerRootFactory serverFactory) {
        DataView<ProductFactory> dataView = new ReferenceAttributeDataView<>(serverFactory.httpServer.get().shopResource.get().products);
        TableView<ProductFactory> dataTableView = new TableView<>();
        DataViewWidget<ProductFactory> widget = new DataViewWidget<>(dataView, dataEditor, uniformDesign, dataTableView);
        return widget.createContent();
    }
}
