package io.github.factoryfx.javafx.factory.widget.factory.diffdialog;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.javafx.factory.css.CssUtil;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.factorydiff.FactoryDiffWidget;
import io.github.factoryfx.javafx.factory.widget.factory.factorylog.FactoryUpdateLogWidget;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class DiffDialogBuilder {

    private LanguageText conflictText= new LanguageText().en("Changes").de("Konflikte");
    private LanguageText changesText= new LanguageText().en("Changes").de("Änderungen");

    private final UniformDesign uniformDesign;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;

    public DiffDialogBuilder(UniformDesign uniformDesign, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder) {
        this.uniformDesign = uniformDesign;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
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
//        final Node diffWidgetContent = factoryDiffWidget.createVisualisation();
//
//        pane.setCenter(diffWidgetContent);
//        pane.setPrefWidth(1000);
//        pane.setPrefHeight(750);
//        dialog.getDialogPane().setContent(pane);
//        dialog.setResizable(true);
//
//        CssUtil.addToNode(dialog.getDialogPane());
//
//        dialog.showAndWait();
//    }


    public void createDiffDialog(MergeDiffInfo<?> mergeDiff, String title, Window owner) {
        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign, attributeVisualisationMappingBuilder);
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

        CssUtil.addToNode(dialog.getDialogPane());

        if (!mergeDiff.hasNoConflicts()){
            dialog.setTitle(uniformDesign.getText(conflictText));
            dialog.setHeaderText(uniformDesign.getText(conflictText));
            diffWidgetContent.getStyleClass().add("error");
        }

        dialog.showAndWait();
    }

    public void createDiffDialog(FactoryUpdateLog factoryLog, String title, Window owner){
        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign, attributeVisualisationMappingBuilder);
        factoryDiffWidget.updateMergeDiff(factoryLog.mergeDiffInfo);

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

        final Tab tabDiff = new Tab(uniformDesign.getText(changesText));
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

        CssUtil.addToNode(dialog.getDialogPane());

        if (!factoryLog.mergeDiffInfo.hasNoConflicts()){
            dialog.setTitle(uniformDesign.getText(conflictText));
            dialog.setHeaderText(uniformDesign.getText(conflictText));
            diffWidgetContent.getStyleClass().add("error");
        }



        dialog.showAndWait();
    }
}
