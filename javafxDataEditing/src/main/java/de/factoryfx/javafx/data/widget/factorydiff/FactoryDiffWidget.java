package de.factoryfx.javafx.data.widget.factorydiff;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class FactoryDiffWidget implements Widget {
    private final UniformDesign uniformDesign;
    private final LanguageText columnFactory=new LanguageText().en("data").de("Objekt");
    private final LanguageText columnField=new LanguageText().en("field").de("Feld");
    private final LanguageText columnPrevious=new LanguageText().en("previous").de("Alt");
    private final LanguageText columnNew=new LanguageText().en("new").de("Neu");
    private final LanguageText titlePrevious=new LanguageText().en("previous value ").de("Alter Wert");
    private final LanguageText titleNew=new LanguageText().en("new value").de("Neuer Wert");
    private final LanguageText noChangesFound=new LanguageText().en("No changes found").de("keine Änderungen gefunden");

    private final AttributeEditorBuilder attributeEditorBuilder;

    public FactoryDiffWidget(UniformDesign uniformDesign, AttributeEditorBuilder attributeEditorBuilder){
        this.uniformDesign=uniformDesign;
        this.attributeEditorBuilder = attributeEditorBuilder;
    }

    private List<AttributeDiffInfoExtended> diff=new ArrayList<>();
    private Consumer<List<AttributeDiffInfoExtended>> diffListUpdater;

    @Override
    public Node createContent() {
        BorderPane previousValueDisplay = new BorderPane();
        previousValueDisplay.getStyleClass().add("dontChangeOpacityIfdisabled");
        BorderPane newValueDisplay = new BorderPane();
        newValueDisplay.getStyleClass().add("dontChangeOpacityIfdisabled");

        TableView<AttributeDiffInfoExtended> diffTableView = createDiffTableViewTable();
//        diffTableView.setFixedCellSize(30.0);
        final ObservableList<AttributeDiffInfoExtended> diffList = FXCollections.observableArrayList();
        diffTableView.setItems(diffList);

        BorderPane borderPane = new BorderPane();

        SplitPane verticalSplitPane = new SplitPane();
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.getItems().add(diffTableView);

        StackPane diffValuesPane = new StackPane();
        final Node previousNode = addTitle(previousValueDisplay, uniformDesign.getText(titlePrevious));
        diffValuesPane.getChildren().add(previousNode);

        final Slider slider = new Slider();


        final Node newNode = addTitle(newValueDisplay, uniformDesign.getText(titleNew));
        diffValuesPane.getChildren().add(newNode);
//        diffValuesPane.setDividerPositions(0.333, 0.6666);

        slider.setMin(0);
        slider.setMax(1);
        slider.setMaxWidth(200);
        newNode.opacityProperty().bind(slider.valueProperty());
        newNode.mouseTransparentProperty().bind(newNode.opacityProperty().isEqualTo(0));

//        newNode.setDisable(true);
//        newNode.getStyleClass().add("dontChangeOpacityIfdisabled");
        previousNode.opacityProperty().bind(slider.valueProperty().add(-1).multiply(-1));

//        previousNode.setDisable(true);
//        previousNode.getStyleClass().add("dontChangeOpacityIfdisabled");

        HBox sliderPane = new HBox(3);
        sliderPane.setPadding(new Insets(3));
        final Button old = new Button("");
        uniformDesign.addIcon(old, FontAwesome.Glyph.ANGLE_DOUBLE_LEFT);
        old.setOnAction(event -> slider.setValue(0));
        sliderPane.getChildren().add(old);
        sliderPane.getChildren().add(slider);
        final Button newButton  = new Button("");
        uniformDesign.addIcon(newButton, FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT);
        newButton.setOnAction(event -> slider.setValue(1));
        sliderPane.getChildren().add(newButton);
        sliderPane.setAlignment(Pos.CENTER);


        VBox vBox=new VBox();
        vBox.getChildren().add(sliderPane);
        VBox.setVgrow(diffValuesPane,Priority.ALWAYS);
        vBox.getChildren().add(diffValuesPane);
        verticalSplitPane.getItems().add(vBox);

        diffTableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            AttributeDiffInfoExtended selectedItem = diffTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Data previousRoot = selectedItem.previousRoot;
                Data newRoot = selectedItem.newRoot;
                AttributeDiffInfo diffItem = selectedItem.attributeDiffInfo;

                Attribute<?,?> previousAttribute = diffItem.getAttribute(previousRoot);
                if (previousAttribute!=null) {
                    previousValueDisplay.setCenter(createEditor(previousAttribute));
                }else {
                    previousValueDisplay.setCenter(null);
                }

                Attribute<?,?> newAttribute = diffItem.getAttribute(newRoot);
                if (newAttribute!=null) {
                    newValueDisplay.setCenter(createEditor(newAttribute));
                } else {
                    newValueDisplay.setCenter(null);
                }
            } else {
                previousValueDisplay.setCenter(null);
                newValueDisplay.setCenter(null);
            }
        });

        diffListUpdater = attributeDiffInfoExtendeds -> {
            diffList.setAll(attributeDiffInfoExtendeds);
            diffTableView.getSelectionModel().selectFirst();
        };
        diffListUpdater.accept(diff);
        borderPane.setCenter(verticalSplitPane);
        return borderPane;
    }

    private Node createEditor(Attribute<?,?> attribute){
        final AttributeEditor<?, ?> previousAttributeEditor = attributeEditorBuilder.getAttributeEditor(attribute, null, null);
        previousAttributeEditor.setReadOnly();
        previousAttributeEditor.expand();
        return previousAttributeEditor.createContent();
    }

    private TableView<AttributeDiffInfoExtended> createDiffTableViewTable(){
        TableView<AttributeDiffInfoExtended> tableView = new TableView<>();
        tableView.setPlaceholder(new Label(uniformDesign.getText(noChangesFound)));
        {
            TableColumn<AttributeDiffInfoExtended, String> factoryColumn = new TableColumn<>(uniformDesign.getText(columnFactory));
            factoryColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().parentDisplayText()));
            tableView.getColumns().add(factoryColumn);

            TableColumn<AttributeDiffInfoExtended, String> fieldColumn = new TableColumn<>(uniformDesign.getText(columnField));
            fieldColumn.setCellValueFactory(param -> {
                try {
                    String labelText = uniformDesign.getLabelText(param.getValue().createPreviousAttribute());

                    return new SimpleStringProperty(labelText);
                } catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            });
            tableView.getColumns().add(fieldColumn);

            TableColumn<AttributeDiffInfoExtended, String> previousColumn = new TableColumn<>(uniformDesign.getText(columnPrevious));
            previousColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPreviousAttributeDisplayText()));
            tableView.getColumns().add(previousColumn);

            TableColumn<AttributeDiffInfoExtended, String> newColumn = new TableColumn<>(uniformDesign.getText(columnNew));
            newColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNewAttributeDisplayText()));
            tableView.getColumns().add(newColumn);

        }




        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory((param) -> new TableRow<>() {
            @Override
            protected void updateItem(AttributeDiffInfoExtended mergeResultEntry, boolean empty) {
                super.updateItem(mergeResultEntry, empty);
                if (mergeResultEntry != null && mergeResultEntry.conflict) {
                    getStyleClass().add("conflictRow");
                } else {
                    getStyleClass().remove("conflictRow");
                }
                if (mergeResultEntry != null && mergeResultEntry.violation) {
                    getStyleClass().add("permissionViolationRow");
                } else {
                    getStyleClass().remove("permissionViolationRow");
                }
            }
        });

        tableView.fixedCellSizeProperty().set(24);

        new TableControlWidget<>(tableView,uniformDesign).hide();
        return tableView;
    }

    private Node addTitle(Node node, String title){
        VBox vBox = new VBox();
        vBox.setSpacing(3);
        Label label = new Label(title);
        label.getStyleClass().add("titleLabel");
        vBox.getChildren().add(label);
        VBox.setVgrow(node, Priority.ALWAYS);
        vBox.getChildren().add(node);
        return vBox;
    }
    public void updateMergeDiff(MergeDiffInfo<?> mergeDiff) {
        Data previousRoot=mergeDiff.getPreviousRootData();
        Data newRoot=mergeDiff.getNewRootData();
        diff = new ArrayList<>();
        diff.addAll(mergeDiff.mergeInfos.stream().map(info->new AttributeDiffInfoExtended(true,false,false, info,previousRoot,newRoot)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.conflictInfos.stream().map(info->new AttributeDiffInfoExtended(false,true,false, info,previousRoot,newRoot)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.permissionViolations.stream().map(info->new AttributeDiffInfoExtended(false,false,true, info,previousRoot,newRoot)).collect(Collectors.toList()));
        if (diffListUpdater!=null){
            diffListUpdater.accept(diff);
        }
    }

}
