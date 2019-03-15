package de.factoryfx.javafx.factory.view.factoryviewmanager;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.data.widget.validation.ValidationWidget;
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
 *
 * @param <R> server root
 */
public class FactoryEditView<R extends FactoryBase<?,R>> implements Widget, FactoryRootChangeListener<R> {

    private final LongRunningActionExecutor LongRunningActionExecutor;
    private final FactoryEditManager<R,?> factoryEditManager;
    private final FactoryAwareWidget<R> content;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private BorderPane borderPane;
    private final DiffDialogBuilder diffDialogBuilder;

    public FactoryEditView(LongRunningActionExecutor longRunningActionExecutor, FactoryEditManager<R,?> factoryEditManager, FactoryAwareWidget<R> content, UniformDesign uniformDesign, DataEditor dataEditor, DiffDialogBuilder diffDialogBuilder) {
        this.LongRunningActionExecutor = longRunningActionExecutor;
        this.factoryEditManager = factoryEditManager;
        this.content = content;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.diffDialogBuilder = diffDialogBuilder;
    }

    @Override
    public Node createContent() {
        factoryEditManager.registerListener(this);
        borderPane = new BorderPane();
        final ToolBar toolBar = new ToolBar();

        {
            final Button save = new Button();
            save.setTooltip(new Tooltip("Änderungen auf den Server speichern"));
            save.setOnAction(event -> {
                Optional<String> result = showCommitDialog(factoryEditManager.getLoadedFactory().get(),dataEditor,uniformDesign);

                result.ifPresent(comment -> {
                    LongRunningActionExecutor.execute(() -> {
                        final FactoryUpdateLog factoryLog = factoryEditManager.save(comment);
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
                                diffDialogBuilder.createDiffDialog(factoryLog, "Gespeicherte Änderungen", save.getScene().getWindow());
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
            check.setTooltip(new Tooltip("Änderungen anzeigen aber nicht speichern"));
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
            resetButton.setTooltip(new Tooltip("löscht alle aktuellen Änderungen"));
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
            saveToFile.setTooltip(new Tooltip("die aktuelle Konfiguartion lokal speichern"));
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
            loadFromFile.setTooltip(new Tooltip("Konfiguration importieren"));
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


        borderPane.setTop(toolBar);

        if (factoryEditManager.getLoadedFactory().isPresent()){
            borderPane.setCenter(content.createContent());
            content.edit((factoryEditManager.getLoadedFactory().get()));
        } else {
            borderPane.setCenter(content.createContent());
            LongRunningActionExecutor.execute(factoryEditManager::load);
        }

        factoryUpdater=content::edit;

        return borderPane;
    }


    private Consumer<R> factoryUpdater;
    @Override
    public void update(Optional<R> previousRoot, R newRoot) {
        previousRoot.ifPresent(serverFactory -> serverFactory.internal().endUsage());
        factoryUpdater.accept(newRoot);
    }

    private Optional<String> showCommitDialog(Data root, DataEditor dataEditor, UniformDesign uniformDesign){
        final ValidationWidget validationWidget = new ValidationWidget(root,dataEditor,uniformDesign);


        Alert  dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(borderPane.getScene().getWindow());
        dialog.setTitle("Speichern");
        dialog.setHeaderText("Speichern");

//        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        final BorderPane pane = new BorderPane();
        pane.setCenter(wrap("Validierung",validationWidget.createContent()));
        final TextArea comment = new TextArea();
        BorderPane.setMargin(comment,new Insets(6,0,0,0));
        comment.setPromptText("Kommentar");
        pane.setBottom(wrap("Kommentar",comment));
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
