package io.github.factoryfx.example.client.view;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidgetFactory;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<JettyServerRootFactory> {
    public final FactoryAttribute<DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryAttribute<>();

    @Override
    protected FactoryAwareWidget<JettyServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
