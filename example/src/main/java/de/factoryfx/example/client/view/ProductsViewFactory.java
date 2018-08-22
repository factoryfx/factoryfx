package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.tree.DataTreeWidget;
import de.factoryfx.javafx.factory.editor.DataEditorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;

public class ProductsViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class);
    public final FactoryReferenceAttribute<DataEditor, DataEditorFactory> dataEditor = new FactoryReferenceAttribute<>(DataEditorFactory.class);

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ProductsView(dataEditor.instance(), uniformDesign.instance());
    }
}
