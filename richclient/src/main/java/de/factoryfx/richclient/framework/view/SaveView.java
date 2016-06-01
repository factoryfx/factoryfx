package de.factoryfx.richclient.framework.view;

import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.richclient.framework.widget.FactoryDiffWidget;
import de.factoryfx.richclient.framework.widget.Widget;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SaveView<T extends FactoryBase<? extends LiveObject, T>> implements Widget {

    private final Function<T,MergeDiff>  factorySaver;
    private final Supplier<T> currentLoadedFactoryProvider;

    public SaveView(Function<T,MergeDiff> factorySaver, Supplier<T> currentLoadedFactoryProvider) {
        this.factorySaver = factorySaver;
        this.currentLoadedFactoryProvider = currentLoadedFactoryProvider;
    }

    @Override
    public Node createContent() {
        FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget();

        VBox vBox = new VBox();
        Button button= new Button("save");
        button.setOnAction(event -> {
            MergeDiff mergeDiff= factorySaver.apply(currentLoadedFactoryProvider.get());
            factoryDiffWidget.updateMergeResult(mergeDiff);
        });
        vBox.getChildren().add(button);
        vBox.getChildren().add(factoryDiffWidget.createContent());
        return vBox;
    }
}
