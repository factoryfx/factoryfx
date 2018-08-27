package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.data.widget.tree.DataTreeWidget;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

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
