package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.ProductFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.dataview.DataViewWidget;
import de.factoryfx.javafx.factory.editor.DataEditorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.dataview.DataViewWidgetFactory;

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
