package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.widget.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factory.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryReferenceAttribute<RichClientRoot,DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryReferenceAttribute<>();

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
