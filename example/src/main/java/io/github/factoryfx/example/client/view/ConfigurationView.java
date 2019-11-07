package io.github.factoryfx.example.client.view;

import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidget;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import javafx.scene.Node;

public class ConfigurationView implements FactoryAwareWidget<JettyServerRootFactory> {

    private final DataTreeWidget dataTreeWidget;

    public ConfigurationView(DataTreeWidget dataTreeWidget) {
        this.dataTreeWidget = dataTreeWidget;
    }

    @Override
    public void edit(JettyServerRootFactory serverFactory) {
        dataTreeWidget.edit(serverFactory);
    }

    @Override
    public Node createContent() {
        return dataTreeWidget.createContent();
    }

}
