package de.factoryfx.example.client.view;

import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.widget.tree.DataTreeWidget;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ConfigurationView implements FactoryAwareWidget<ShopFactory> {

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;

    public ConfigurationView(UniformDesign uniformDesign, DataEditor dataEditor) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
    }

    @Override
    public Node init(ShopFactory serverFactory) {
        StackPane root = new StackPane();

        BorderPane content = new BorderPane();
        root.getChildren().add(content);

        content.setCenter(new DataTreeWidget(dataEditor, serverFactory, uniformDesign).createContent());
        dataEditor.edit(serverFactory);
        return content;
    }
}
