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
    public Node init(ServerRootFactory serverFactory) {
        StackPane root = new StackPane();

        BorderPane content = new BorderPane();
        root.getChildren().add(content);

        content.setCenter(dataTreeWidget.createContent());
        dataTreeWidget.edit(serverFactory);
        return content;
    }

//    @Override
//    public Node update(ServerRootFactory newFactory) {
////        System.out.println("asadsad");
//        dataTreeWidget.edit(newFactory);
//        return null;
//    }
}
