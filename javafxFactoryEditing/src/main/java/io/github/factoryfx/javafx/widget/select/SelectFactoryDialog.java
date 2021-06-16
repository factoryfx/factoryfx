package io.github.factoryfx.javafx.widget.select;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.PossibleNewValue;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.util.ObservableFactoryDisplayText;
import io.github.factoryfx.javafx.util.UniformDesign;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SelectFactoryDialog<F extends FactoryBase<?,?>> {
    public final List<PossibleNewValue<F>> dataList;
    private final UniformDesign uniformDesign;

    public SelectFactoryDialog(List<PossibleNewValue<F>> dataList, UniformDesign uniformDesign) {
        this.dataList = dataList;
        this.uniformDesign = uniformDesign;
    }

    public void show(Window owner, Consumer<PossibleNewValue<F>> success){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Select");
        dialog.setHeaderText("Select");

//        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final BorderPane pane = new BorderPane();
        TableView<PossibleNewValue<F>> table = new TableView<>();
        table.getItems().setAll(dataList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<PossibleNewValue<F>, String> column = new TableColumn<>();
        column.setCellValueFactory(param -> new ObservableFactoryDisplayText(param.getValue().newValue));
        table.getColumns().add(column);
        pane.setCenter(table);

        double centerX = owner.getX()+owner.getWidth()/2;
        double centerY = owner.getY()+owner.getHeight()/2;
        Screen screen = Screen.getScreens().stream().filter(s->s.getBounds().contains(centerX,centerY)).findFirst().orElse(Screen.getPrimary());
        Rectangle2D screenBounds = screen.getBounds();
        pane.setPrefWidth(screenBounds.getWidth()/3);
        pane.setPrefHeight(screenBounds.getHeight()/2);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);

        CssUtil.addToNode(dialog.getDialogPane());
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        final Optional<ButtonType> dialogResult = dialog.showAndWait();
        if (dialogResult.get() == ButtonType.OK && table.getSelectionModel().getSelectedItem()!=null){
            success.accept(table.getSelectionModel().getSelectedItem());
        }
    }

}
