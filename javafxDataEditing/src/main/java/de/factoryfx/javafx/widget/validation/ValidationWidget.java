package de.factoryfx.javafx.widget.validation;

import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class ValidationWidget implements Widget {

    private LanguageText columnData=new LanguageText().en("data").de("Objekt");
    private LanguageText columnField=new LanguageText().en("field").de("Feld");
    private LanguageText columnValidation=new LanguageText().en("validation").de("Validierung");
    private LanguageText refresh=new LanguageText().en("refresh").de("Aktualisieren");

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

        public ValidationAndData(ValidationError validationError, Data data) {
            this.validationError = validationError;
            this.data = data;
        }
    }

    @Override
    public Node createContent() {
        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        final TableView<ValidationAndData> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        {
            final TableColumn<ValidationAndData, String> column = new TableColumn<>(uniformDesign.getText(columnData));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().data.internal().getDisplayText()));
            tableView.getColumns().add(column);
        }
        {
            final TableColumn<ValidationAndData, String> column = new TableColumn<>(uniformDesign.getText(columnField));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().validationError.attributeDescription(uniformDesign::getText)));
            tableView.getColumns().add(column);
        }
        {
            final TableColumn<ValidationAndData, String> column = new TableColumn<>(uniformDesign.getText(columnValidation));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().validationError.validationDescription(uniformDesign::getText)));
            tableView.getColumns().add(column);
        }

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                dataEditor.edit(newValue.data);
                dataEditor.setHistory(newValue.data.internal().getPathFromRoot());
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

//        SplitPane.setResizableWithParent(vBox, Boolean.TRUE);
        splitPane.getItems().add(vBox);

        splitPane.getItems().add( dataEditor.createContent());

        splitPane.disableProperty().bind(isValid);
        return splitPane;
    }

    private void validate(TableView<ValidationAndData> tableView){
        Data selected= dataEditor.editData().get();
        tableView.getItems().clear();
        for (Data data: root.internal().collectChildrenDeep()){
            tableView.getItems().addAll(data.internal().validateFlat().stream().map(validationError->new ValidationAndData(validationError,data)).collect(Collectors.toList()));
        }
        tableView.getStyleClass().remove("error");
        if (!tableView.getItems().isEmpty()){
            tableView.getStyleClass().add("error");
        }
        tableView.getItems().forEach(validationAndData -> {
            if (validationAndData.data==selected){
                tableView.getSelectionModel().select(validationAndData);
            }
        });
        isValid.set(tableView.getItems().isEmpty());
    }

    SimpleBooleanProperty isValid=new SimpleBooleanProperty();
    public ReadOnlyBooleanProperty isValid(){
        return isValid;
    }
}
