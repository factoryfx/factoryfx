package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidget;
import javafx.scene.Node;

public class ConfigurationView implements FactoryAwareWidget<ServerRootFactory> {

    private final DataTreeWidget dataTreeWidget;

    public ConfigurationView(DataTreeWidget dataTreeWidget) {
        this.dataTreeWidget = dataTreeWidget;
    }

    @Override
    public void edit(ServerRootFactory serverFactory) {
        dataTreeWidget.edit(serverFactory);
    }

    @Override
    public Node createContent() {
        return dataTreeWidget.createContent();
    }

}
