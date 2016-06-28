package de.factoryfx.richclient.framework.view;

import java.util.function.Supplier;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.richclient.FactoryTreeEditor;
import de.factoryfx.richclient.framework.widget.Widget;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class LoadView<T extends FactoryBase<? extends LiveObject, T>> implements Widget {
    private final FactoryTreeEditor<T> factoryTreeEditor;
    private final Supplier<T> factoryLoader;

    public LoadView(FactoryTreeEditor<T> factoryTreeEditor, Supplier<T> factoryLoader) {
        this.factoryTreeEditor = factoryTreeEditor;
        this.factoryLoader = factoryLoader;
    }

    @Override
    public Node createContent() {

        HBox hBox = new HBox();
        Button button= new Button("load");
        button.setOnAction(event ->
                factoryTreeEditor.bind(factoryLoader.get())
        );
        hBox.getChildren().add(button);
        return hBox;

    }
}
