package de.factoryfx.javafx.widget.diffdialog;

import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.factorydiff.FactoryDiffWidget;
import de.factoryfx.javafx.widget.factorylog.FactoryUpdateLogWidget;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class DiffDialogBuilder {

    private final UniformDesign uniformDesign;
    private final AttributeEditorBuilder attributeEditorBuilder;

    public DiffDialogBuilder(UniformDesign uniformDesign, AttributeEditorBuilder attributeEditorBuilder) {
        this.uniformDesign = uniformDesign;
        this.attributeEditorBuilder = attributeEditorBuilder;
    }

//    public void createDiffDialog(List<AttributeDiffInfo> diffs, String title, Window owner) {
//        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign,attributeEditorBuilder);
//        factoryDiffWidget.updateMergeDiff(diffs);
//
//
//        Dialog<Void> dialog = new Dialog<>();
//        dialog.initOwner(owner);
//        dialog.setTitle(title);
//        dialog.setHeaderText(title);
//
//        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType/*, ButtonType.CANCEL*/);
//
//        final BorderPane pane = new BorderPane();
//        final Node diffWidgetContent = factoryDiffWidget.createContent();
//
//        pane.setCenter(diffWidgetContent);
//        pane.setPrefWidth(1000);
//        pane.setPrefHeight(750);
//        dialog.getDialogPane().setContent(pane);
//        dialog.setResizable(true);
//
//        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
//
//        dialog.showAndWait();
//    }


    public void createDiffDialog(MergeDiffInfo mergeDiff, String title, Window owner) {
        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign,attributeEditorBuilder);
        factoryDiffWidget.updateMergeDiff(mergeDiff);


        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.setHeaderText(title);

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType/*, ButtonType.CANCEL*/);

        final BorderPane pane = new BorderPane();
        final Node diffWidgetContent = factoryDiffWidget.createContent();

        pane.setCenter(diffWidgetContent);
        pane.setPrefWidth(1000);
        pane.setPrefHeight(750);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());

        if (!mergeDiff.hasNoConflicts()){
            dialog.setTitle("Konflikte");
            dialog.setHeaderText("Konflikte");
            diffWidgetContent.getStyleClass().add("error");
        }



        dialog.showAndWait();
    }

    public void createDiffDialog(Data root, FactoryUpdateLog factoryLog, String title, Window owner){
        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign,attributeEditorBuilder);
        factoryDiffWidget.updateMergeDiff(root,factoryLog.mergeDiffInfo);

        final FactoryUpdateLogWidget factoryLogWidget = new FactoryUpdateLogWidget(uniformDesign);
        factoryLogWidget.updateLog(factoryLog);

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.setHeaderText(title);

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType/*, ButtonType.CANCEL*/);

        final BorderPane pane = new BorderPane();
        final Node diffWidgetContent = factoryDiffWidget.createContent();
        final TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("floating");

        final Tab tabDiff = new Tab("Änderungen");
        tabDiff.setClosable(false);
        tabDiff.setContent(diffWidgetContent);
        tabPane.getTabs().addAll(tabDiff);

        final Tab tabLog = new Tab("Log");
        tabLog.setClosable(false);
        tabLog.setContent(factoryLogWidget.createContent());
        tabPane.getTabs().addAll(tabLog);

        pane.setCenter(tabPane);
        pane.setPrefWidth(1000);
        pane.setPrefHeight(750);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());

        if (!factoryLog.mergeDiffInfo.hasNoConflicts()){
            dialog.setTitle("Konflikte");
            dialog.setHeaderText("Konflikte");
            diffWidgetContent.getStyleClass().add("error");
        }



        dialog.showAndWait();
    }
}
