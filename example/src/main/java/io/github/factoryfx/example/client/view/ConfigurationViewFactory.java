package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.widget.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryAttribute<RichClientRoot,DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
