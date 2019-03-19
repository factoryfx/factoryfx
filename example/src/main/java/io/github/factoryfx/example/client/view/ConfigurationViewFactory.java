package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.widget.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryReferenceAttribute<DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryReferenceAttribute<>(DataTreeWidgetFactory.class);

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
