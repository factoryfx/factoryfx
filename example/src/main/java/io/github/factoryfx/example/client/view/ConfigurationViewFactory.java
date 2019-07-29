package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryAttribute<DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
