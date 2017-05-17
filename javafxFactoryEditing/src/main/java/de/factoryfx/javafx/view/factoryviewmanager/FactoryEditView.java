package de.factoryfx.javafx.view.factoryviewmanager;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.LongRunningActionExecutor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.widget.validation.ValidationWidget;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
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
public class FactoryEditView<V,R extends FactoryBase<?,V>> implements Widget, FactoryRootChangeListener<R> {

    private final LongRunningActionExecutor LongRunningActionExecutor;
    private final FactoryEditManager<V,R> factoryManager;
    private final FactoryAwareWidget<R> content;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private BorderPane borderPane;
    private final DiffDialogBuilder diffDialogBuilder;
    private final SimpleObjectProperty<Data> selectedFactory;

    public FactoryEditView(LongRunningActionExecutor longRunningActionExecutor, FactoryEditManager<V,R> factoryManager, FactoryAwareWidget content, UniformDesign uniformDesign, DataEditor dataEditor, DiffDialogBuilder diffDialogBuilder) {
        this.LongRunningActionExecutor = longRunningActionExecutor;
        this.factoryManager = factoryManager;
        this.content = content;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.diffDialogBuilder = diffDialogBuilder;
        this.selectedFactory = content.selectedFactory();
    }

    @Override
    public void closeNotifier() {
        factoryManager.removeListener(this);
    }

    @Override
    public Node createContent() {
        factoryManager.registerListener(this);
        borderPane = new BorderPane();
        final ToolBar toolBar = new ToolBar();

        {
            final Button save = new Button();
            save.setTooltip(new Tooltip("Änderungen auf den Server speichern"));
            save.setOnAction(event -> {
                Optional<String> result = showCommitDialog(factoryManager.getLoadedFactory().get(),dataEditor,uniformDesign);

                if (result.isPresent()) {
                    String comment = result.get();
                    LongRunningActionExecutor.execute(() -> {
                        final FactoryUpdateLog factoryLog = factoryManager.save(comment);
                        Platform.runLater(() -> {
                            diffDialogBuilder.createDiffDialog(factoryLog, "Gespeicherte Änderungen",save.getScene().getWindow());
                        });
                    });
                }
            });
            uniformDesign.addIcon(save, FontAwesome.Glyph.SAVE);
            toolBar.getItems().add(save);
        }
        {
            final Button check = new Button();
            check.setTooltip(new Tooltip("Änderungen anzeigen aber nicht speichern"));
            check.setOnAction(event -> {
                LongRunningActionExecutor.execute(() -> {
                    final MergeDiffInfo mergeDiff = factoryManager.simulateUpdateCurrentFactory();
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

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    LongRunningActionExecutor.execute(() -> {
                        factoryManager.reset();
                    });
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
                        factoryManager.saveToFile(file.toPath());
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
                        factoryManager.loadFromFile(file.toPath());
                    });
                }
            });
            toolBar.getItems().add(loadFromFile);
            toolBar.getItems().add(new Separator());
        }

        {
            final Button factoryHistory = new Button("");
            factoryHistory.setTooltip(new Tooltip("Factory History"));
            uniformDesign.addIcon(factoryHistory, FontAwesome.Glyph.HISTORY);
            factoryHistory.setOnAction(event -> {

                LongRunningActionExecutor.execute(() -> {
                    LongRunningActionExecutor.execute(() -> {
                        final List<AttributeDiffInfo> diff = factoryManager.getSingleFactoryHistory(selectedFactory.get().getId());
                        Platform.runLater(() -> {
                            diffDialogBuilder.createDiffDialog(diff, "Änderungen",factoryHistory.getScene().getWindow());
                        });
                    });
                    factoryManager.reset();
                });
            });
            factoryHistory.disableProperty().bind(selectedFactory.isNull());
            toolBar.getItems().add(factoryHistory);
        }


        borderPane.setTop(toolBar);

        if (factoryManager.getLoadedFactory().isPresent()){
            borderPane.setCenter(content.init(factoryManager.getLoadedFactory().get()));
        } else {
//            StackPane stackPane = new StackPane();
//            Button loadButton = new Button("Daten laden");
//            loadButton.setOnAction(event -> {
//                LongRunningActionExecutor.execute(() -> {
//                    factoryManager.load();
//                });
//            });
//            stackPane.getChildren().add(loadButton);
//            borderPane.setCenter(stackPane);
            LongRunningActionExecutor.execute(() -> {
                factoryManager.load();
            });
        }

        factoryUpdater=serverFactory -> {
            borderPane.setCenter(content.init(serverFactory));
        };

        return borderPane;
    }


    Consumer<R> factoryUpdater;
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

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());

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
