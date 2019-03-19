package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import io.github.factoryfx.javafx.data.widget.tree.DataTreeWidget;
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
