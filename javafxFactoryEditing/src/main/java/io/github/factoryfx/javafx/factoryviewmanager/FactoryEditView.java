package io.github.factoryfx.javafx.factoryviewmanager;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.io.File;
import java.util.Optional;


import io.github.factoryfx.factory.attribute.types.I18nAttribute;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.javafx.css.CssUtil;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.widget.validation.ValidationWidget;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;

/**
 * Lifecycle contract towards the wrapped {@link FactoryAwareWidget}:
 * <ul>
 *   <li>{@code content.createContent()} is called on every view open, before the factory is available,
 *       so widgets can display immediately and start loading non-factory data in parallel with the
 *       factory download.</li>
 *   <li>{@code content.edit(root)} is called at most once per root instance: after the initial factory
 *       load and after every save/update that produces a new root. Re-opening the view with an unchanged
 *       factory does NOT call {@code edit} again.</li>
 * </ul>
 *
 * @param <R> server root
 */
public class FactoryEditView<R extends FactoryBase<?,R>> implements Widget, FactoryRootChangeListener<R> {

    public final I18nAttribute saveTooltip = new I18nAttribute().de("Änderungen auf den Server speichern").en("Apply changes to server");
    public final I18nAttribute savedChanges = new I18nAttribute().de("Gespeicherte Änderungen").en("Applied changes");
    public final I18nAttribute checkTooltip = new I18nAttribute().de("Änderungen anzeigen aber nicht speichern").en("Show changes (without applying them)");
    public final I18nAttribute resetTooltip = new I18nAttribute().de("löscht alle aktuellen Änderungen").en("Reset local client changes");
    public final I18nAttribute saveToFileTooltip = new I18nAttribute().de("die aktuelle Konfiguartion lokal speichern").en("Save the current configuration locally");
    public final I18nAttribute loadFromFileTooltip = new I18nAttribute().de("Konfiguration importieren").en("Import configuration from file");
    public final I18nAttribute validation = new I18nAttribute().de("Validierung").en("Validation");
    public final I18nAttribute comment = new I18nAttribute().de("Kommentar").en("Comment");
    public final I18nAttribute save = new I18nAttribute().de("Speichern").en("Save");

    private final LongRunningActionExecutor LongRunningActionExecutor;
    private final FactoryEditManager<R> factoryEditManager;
    private final FactoryAwareWidget<R> content;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private BorderPane borderPane;
    private final DiffDialogBuilder<R> diffDialogBuilder;
    private R lastEditedRoot;

    public FactoryEditView(LongRunningActionExecutor longRunningActionExecutor, FactoryEditManager<R> factoryEditManager, FactoryAwareWidget<R> content, UniformDesign uniformDesign, DataEditor dataEditor, DiffDialogBuilder<R> diffDialogBuilder) {
        this.LongRunningActionExecutor = longRunningActionExecutor;
        this.factoryEditManager = factoryEditManager;
        this.content = content;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.diffDialogBuilder = diffDialogBuilder;
    }

    @Override
    public Node createContent() {
        if (borderPane == null) {
            //one-time setup: repeated registerListener calls on every open accumulated duplicate
            //listeners, so a single save fired content.edit() once per previous open
            factoryEditManager.registerListener(this);
            borderPane = new BorderPane();
            borderPane.setTop(createToolBar());
        }

        //every open: widgets typically return a cached node and use this call to refresh their data
        borderPane.setCenter(content.createContent());

        if (factoryEditManager.getLoadedFactory().isPresent()){
            editIfNewRoot(factoryEditManager.getLoadedFactory().get());
        } else {
            LongRunningActionExecutor.execute(factoryEditManager::load);
        }

        return borderPane;
    }

    private void editIfNewRoot(R root) {
        if (root == lastEditedRoot) {
            return; //unchanged factory (e.g. view re-opened): edit() is only called for new root instances
        }
        lastEditedRoot = root;
        content.edit(root);
    }

    /** creates the save/check/reset/export/import toolbar; overridable for headless tests */
    protected ToolBar createToolBar() {
        final ToolBar toolBar = new ToolBar();

        {
            final Button save = new Button();
            save.setTooltip(new Tooltip(uniformDesign.getText(saveTooltip)));
            save.setOnAction(event -> {
                Optional<String> result = showCommitDialog(factoryEditManager.getLoadedFactory().get(),dataEditor,uniformDesign);

                result.ifPresent(comment -> {
                    LongRunningActionExecutor.execute(() -> {
                        final FactoryUpdateLog<R> factoryLog = factoryEditManager.save(comment);
                        if (factoryLog.failedUpdate()){
                            Platform.runLater(()->{
                                Alert alter = new Alert(Alert.AlertType.ERROR);
                                alter.setContentText("Server Error. Reset to previous configuration");
                                TextArea textArea = new TextArea();
                                textArea.setText(factoryLog.exception);
                                alter.setGraphic(textArea);
                                alter.show();
                            });
                        } else {
                            Platform.runLater(() -> {
                                diffDialogBuilder.createDiffDialog(factoryLog, uniformDesign.getText(savedChanges), save.getScene().getWindow());
                            });
                        }
                    });
                });
            });
            uniformDesign.addIcon(save, FontAwesome.Glyph.SAVE);
            toolBar.getItems().add(save);
        }
        {
            final Button check = new Button();
            check.setTooltip(new Tooltip(uniformDesign.getText(checkTooltip)));
            check.setOnAction(event -> {
                LongRunningActionExecutor.execute(() -> {
                    final MergeDiffInfo<R> mergeDiff = factoryEditManager.simulateUpdateCurrentFactory();
                    Platform.runLater(() -> {
                        diffDialogBuilder.createDiffDialog(mergeDiff, "ungespeicherte Änderungen",check.getScene().getWindow());
                    });
                });
            });
            uniformDesign.addIcon(check, FontAwesome.Glyph.QUESTION);
            toolBar.getItems().add(check);
        }


        {
            final Button resetButton = new Button();
            resetButton.setTooltip(new Tooltip(uniformDesign.getText(resetTooltip)));
            uniformDesign.addDangerIcon(resetButton, FontAwesome.Glyph.UNDO);
            resetButton.setOnAction(event -> {
                Alert alert = new Alert(CONFIRMATION);
                alert.setTitle("Änderungen löschen");
                alert.setHeaderText("Änderungen löschen");
                alert.setContentText("Alle Änderungen werden verworfen");
                alert.initOwner(resetButton.getScene().getWindow());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    LongRunningActionExecutor.execute(factoryEditManager::reset);
                }
            });
            toolBar.getItems().add(new Separator());
            toolBar.getItems().add(resetButton);
        }

        {
            final Button saveToFile = new Button("export");
            saveToFile.setTooltip(new Tooltip( uniformDesign.getText(saveToFileTooltip)));
            uniformDesign.addIcon(saveToFile, FontAwesome.Glyph.HDD_ALT);
            saveToFile.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json (*.json)", "*.json");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(saveToFile.getScene().getWindow());

                if (file!=null){
                    LongRunningActionExecutor.execute(() -> {
                        factoryEditManager.saveToFile(file.toPath());
                    });
                }
            });
            toolBar.getItems().add(new Separator());
            toolBar.getItems().add(saveToFile);
        }

        {
            final Button loadFromFile = new Button("import");
            loadFromFile.setTooltip(new Tooltip(uniformDesign.getText(loadFromFileTooltip)) );
            uniformDesign.addIcon(loadFromFile, FontAwesome.Glyph.HDD_ALT);
            loadFromFile.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json (*.json)", "*.json");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(loadFromFile.getScene().getWindow());

                if (file!=null){
                    LongRunningActionExecutor.execute(() -> {
                        factoryEditManager.loadFromFile(file.toPath());
                    });
                }
            });
            toolBar.getItems().add(loadFromFile);
            toolBar.getItems().add(new Separator());
        }

        return toolBar;
    }

    @Override
    public void update(Optional<R> previousRoot, R newRoot) {
        previousRoot.ifPresent(serverFactory -> serverFactory.internal().endEditingDeepFromRoot());
        editIfNewRoot(newRoot);
    }

    private Optional<String> showCommitDialog(FactoryBase<?,?> root, DataEditor dataEditor, UniformDesign uniformDesign){
        final ValidationWidget validationWidget = new ValidationWidget(root,dataEditor,uniformDesign);


        Alert  dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(borderPane.getScene().getWindow());
        dialog.setTitle(uniformDesign.getText(save));
        dialog.setHeaderText(uniformDesign.getText(save));

        final BorderPane pane = new BorderPane();
        pane.setCenter(wrap(uniformDesign.getText(validation),validationWidget.createContent()));
        final TextArea comment = new TextArea();
        BorderPane.setMargin(comment,new Insets(6,0,0,0));
        comment.setPromptText(uniformDesign.getText(this.comment));
        pane.setBottom(wrap(uniformDesign.getText(this.comment),comment));
        pane.setPrefWidth(1000);
        pane.setPrefHeight(750);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);


        CssUtil.addToNode(dialog.getDialogPane());

        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validationWidget.isValid().not().or(comment.textProperty().isEmpty()));


        dialog.showAndWait();

        if (dialog.getResult()==ButtonType.CANCEL || dialog.getResult()==null){
            return Optional.empty();
        }
        return Optional.of(comment.getText());

    }

    private Node wrap(String title, Node node){
        final VBox vBox = new VBox(3);
        vBox.getChildren().addAll(new Label(title),node);
        return vBox;
    }

}
