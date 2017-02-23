package de.factoryfx.javafx.widget.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class ValidationWidget implements Widget {

    private LanguageText columnData=new LanguageText().en("data").de("Objekt");
    private LanguageText columnField=new LanguageText().en("field").de("Feld");
    private LanguageText columnValidation=new LanguageText().en("validation").de("Validierung");
    private LanguageText refresh=new LanguageText().en("refresh").de("Aktualisieren");
    private LanguageText noerror=new LanguageText().en("no errors found").de("Keine Fehler gefunden");

    private final Data root;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;

    public ValidationWidget(Data root, DataEditor dataEditor, UniformDesign uniformDesign){
        this.root = root;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
    }

    private static class ValidationAndData{
        public final ValidationError validationError;
        public final Data data;
        public final String dataDisplayText;

        public ValidationAndData(ValidationError validationError, Data data, String dataDisplayText) {
            this.validationError = validationError;
            this.data = data;
            this.dataDisplayText = dataDisplayText;
        }
    }

    @Override
    public Node createContent() {
        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        final TreeTableView<ValidationAndData> tableView = new TreeTableView<>();
        tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        {
            final TreeTableColumn<ValidationAndData, String> column = new TreeTableColumn<>(uniformDesign.getText(columnData));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().dataDisplayText));
            tableView.getColumns().add(column);
        }
        {
            final TreeTableColumn<ValidationAndData, String> column = new TreeTableColumn<>(uniformDesign.getText(columnField));
            column.setCellValueFactory(param -> {
                String initialValue = "";
                if (param.getValue().getValue().validationError!=null){
                    initialValue = param.getValue().getValue().validationError.attributeDescription(uniformDesign::getText);
                }
                return new SimpleStringProperty(initialValue);
            });
            tableView.getColumns().add(column);
        }
        {
            final TreeTableColumn<ValidationAndData, String> column = new TreeTableColumn<>(uniformDesign.getText(columnValidation));
            column.setCellValueFactory(param -> {
                        String initialValue = "";
                        if (param.getValue().getValue().validationError!=null){
                            initialValue = param.getValue().getValue().validationError.validationDescription(uniformDesign::getText);
                        }
                        return new SimpleStringProperty(initialValue);
            });
            tableView.getColumns().add(column);
        }


        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                dataEditor.edit(newValue.getValue().data);
                dataEditor.setHistory(newValue.getValue().data.internal().getPathFromRoot());
            } else {
                dataEditor.edit(null);
                dataEditor.resetHistory();
            }
        });

        VBox vBox = new VBox();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().add(tableView);
        final HBox buttons = new HBox();
        buttons.setPadding(new Insets(3));
        final Button refreshButton = new Button(uniformDesign.getText(refresh));
        uniformDesign.addIcon(refreshButton, FontAwesome.Glyph.REFRESH);
        refreshButton.setOnAction((a)->validate(tableView));
        buttons.getChildren().add(refreshButton);
        vBox.getChildren().add(buttons);

        validate(tableView);
        tableView.getRoot().setExpanded(true);

//        SplitPane.setResizableWithParent(vBox, Boolean.TRUE);
        splitPane.getItems().add(vBox);

        final ScrollPane scrollPane = new ScrollPane(dataEditor.createContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        splitPane.getItems().add(scrollPane);

        splitPane.disableProperty().bind(isValid);
        final BorderPane borderPane = new BorderPane();
        final InvalidationListener invalidationListener = observable -> {
            if (!isValid.get()) {
                borderPane.setCenter(splitPane);
            } else {
                final Label noErrorLabel = new Label(uniformDesign.getText(noerror));
                noErrorLabel.setGraphic(uniformDesign.createIconSuccess(FontAwesome.Glyph.CHECK));
                borderPane.setCenter(noErrorLabel);
            }
        };
        isValid.addListener(invalidationListener);
        invalidationListener.invalidated(isValid);
        return borderPane;
    }

    private void validate(TreeTableView<ValidationAndData> tableView){
        final TreeItem<ValidationAndData> root = new TreeItem<>();
        tableView.setRoot(root);
        tableView.setShowRoot(false);

        List<ValidationError> validationErrors= new ArrayList<>();
        for (Data data: this.root.internal().collectChildrenDeep()){
            TreeItem<ValidationAndData> dataItem = new TreeItem<>();
            dataItem.setExpanded(true);
            data.internal().validateFlat().forEach(validationError->{
                validationErrors.add(validationError);
                TreeItem<ValidationAndData> error =new TreeItem<>(new ValidationAndData(validationError,data,data.internal().getDisplayText()));
                dataItem.getChildren().add(error);
            });
            if (!dataItem.getChildren().isEmpty()){
                dataItem.setValue(new ValidationAndData(null,data,data.internal().getPathFromRoot().stream().map(d->d.internal().getDisplayText()).collect(Collectors.joining("/"))));
                root.getChildren().add(dataItem);
            }
        }
        tableView.getStyleClass().remove("error");
        if (!validationErrors.isEmpty()){
            tableView.getStyleClass().add("error");
        }
        tableView.getSelectionModel().clearSelection();
        dataEditor.reset();
        isValid.set(validationErrors.isEmpty());
    }

    SimpleBooleanProperty isValid=new SimpleBooleanProperty();
    public ReadOnlyBooleanProperty isValid(){
        return isValid;
    }
}
