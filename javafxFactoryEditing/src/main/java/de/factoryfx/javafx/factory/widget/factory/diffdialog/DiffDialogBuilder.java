package de.factoryfx.javafx.factory.widget.factory.diffdialog;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.factorydiff.FactoryDiffWidget;
import de.factoryfx.javafx.factory.widget.factory.factorylog.FactoryUpdateLogWidget;
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
    private LanguageText refreshText= new LanguageText().en("Refresh").de("Aktualisieren");

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

        CssUtil.addToNode(dialog.getDialogPane());

        if (!mergeDiff.hasNoConflicts()){
            dialog.setTitle(uniformDesign.getText(conflictText));
            dialog.setHeaderText(uniformDesign.getText(conflictText));
            diffWidgetContent.getStyleClass().add("error");
        }

        dialog.showAndWait();
    }

    public void createDiffDialog(FactoryUpdateLog factoryLog, String title, Window owner){
        final FactoryDiffWidget factoryDiffWidget = new FactoryDiffWidget(uniformDesign,attributeEditorBuilder);
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
