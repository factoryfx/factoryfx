package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.widget.tree.DataTreeWidget;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory<ServerRootFactory> {
    public final FactoryReferenceAttribute<DataTreeWidget, DataTreeWidgetFactory> dataTreeWidget = new FactoryReferenceAttribute<>(DataTreeWidgetFactory.class);

    @Override
    protected FactoryAwareWidget<ServerRootFactory> createWidget() {
        return new ConfigurationView(dataTreeWidget.instance());
    }
}
