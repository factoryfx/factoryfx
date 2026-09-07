package io.github.factoryfx.javafx.widget.factory.diffdialog;

import java.util.List;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.factorydiff.FactoryDiffWidget;
import io.github.factoryfx.javafx.widget.factory.factorylog.FactoryUpdateLogWidget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class DiffDialogBuilder<RS extends FactoryBase<?,RS>> {

    private LanguageText conflictText= new LanguageText().en("Changes").de("Konflikte");
    private LanguageText changesText= new LanguageText().en("Changes").de("Änderungen");
    private LanguageText validationFailedText= new LanguageText().en("Validation failed").de("Validierung fehlgeschlagen");
    private LanguageText validationFailedNotSavedText= new LanguageText().en("Validation failed - the changes were not applied").de("Validierung fehlgeschlagen - die Änderungen wurden nicht gespeichert");

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


    public void createDiffDialog(MergeDiffInfo<RS> mergeDiff, String title, Window owner) {
        final FactoryDiffWidget<RS> factoryDiffWidget = new FactoryDiffWidget<>(uniformDesign, attributeVisualisationMappingBuilder);
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

        if (mergeDiff.hasValidationErrors()){
            dialog.setTitle(uniformDesign.getText(validationFailedText));
            dialog.setHeaderText(uniformDesign.getText(validationFailedText));
            TextArea validationErrorsArea = createValidationErrorsTextArea(mergeDiff.validationErrors);
            validationErrorsArea.setPrefRowCount(6);
            BorderPane.setMargin(validationErrorsArea,new Insets(0,0,6,0));
            pane.setTop(validationErrorsArea);
        }

        dialog.showAndWait();
    }

    public void createDiffDialog(FactoryUpdateLog<RS> factoryLog, String title, Window owner){
        if (factoryLog.failedValidation()){
            //the update was rejected by server validation: there is no merge result to show (mergeDiffInfo is null)
            createValidationErrorDialog(factoryLog.validationErrors, owner);
            return;
        }
        final FactoryDiffWidget<RS> factoryDiffWidget = new FactoryDiffWidget<>(uniformDesign, attributeVisualisationMappingBuilder);
        factoryDiffWidget.updateMergeDiff(factoryLog.mergeDiffInfo);

        final FactoryUpdateLogWidget<RS> factoryLogWidget = new FactoryUpdateLogWidget<>();
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

    private void createValidationErrorDialog(List<String> validationErrors, Window owner){
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(uniformDesign.getText(validationFailedNotSavedText));
        dialog.setHeaderText(uniformDesign.getText(validationFailedNotSavedText));
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));

        final BorderPane pane = new BorderPane();
        pane.setCenter(createValidationErrorsTextArea(validationErrors));
        pane.setPrefWidth(1000);
        pane.setPrefHeight(500);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);

        CssUtil.addToNode(dialog.getDialogPane());

        dialog.showAndWait();
    }

    private TextArea createValidationErrorsTextArea(List<String> validationErrors){
        TextArea textArea = new TextArea(String.join("\n\n", validationErrors));
        textArea.setEditable(false);
        textArea.getStyleClass().add("error");
        return textArea;
    }
}
