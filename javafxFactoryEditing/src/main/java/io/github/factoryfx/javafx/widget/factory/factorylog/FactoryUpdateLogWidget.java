package io.github.factoryfx.javafx.widget.factory.factorylog;

import java.util.function.Consumer;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.factory.log.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class FactoryUpdateLogWidget<R extends FactoryBase<?, R>> implements Widget {

    private Consumer<FactoryUpdateLog<R>> factoryLogRootUpdater;
    FactoryUpdateLog<R> factoryLog;

    public void updateLog(FactoryUpdateLog<R> factoryLog) {
        this.factoryLog=factoryLog;
        if (factoryLogRootUpdater!=null){
            factoryLogRootUpdater.accept(factoryLog);
        }
    }

    @Override
    public Node createContent() {
        final BorderPane borderPane = new BorderPane();
        factoryLogRootUpdater= root -> {
            TextArea textArea = new TextArea();
            textArea.setText(factoryLog.log);
            borderPane.setCenter(textArea);
            final Label totalDuarion = new Label("total edit duration: " + (factoryLog.totalDurationNs / 1000000.0) + "ms");
            BorderPane.setMargin(totalDuarion,new Insets(3));
            borderPane.setTop(totalDuarion);
        };
        if (factoryLog!=null) {
            factoryLogRootUpdater.accept(factoryLog);
        }
        return borderPane;
    }

}
