package io.github.factoryfx.javafx.widget.factory.factorylog;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.table.TableControlWidget;
import io.github.factoryfx.factory.log.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.controlsfx.glyphfont.FontAwesome;

public class FactoryUpdateLogWidget implements Widget {
    private UniformDesign uniformDesign;

    public FactoryUpdateLogWidget(UniformDesign uniformDesign){
        this.uniformDesign=uniformDesign;
    }

    private Consumer<FactoryUpdateLog> factoryLogRootUpdater;
    FactoryUpdateLog<?> factoryLog;

    public void updateLog(FactoryUpdateLog factoryLog) {
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
