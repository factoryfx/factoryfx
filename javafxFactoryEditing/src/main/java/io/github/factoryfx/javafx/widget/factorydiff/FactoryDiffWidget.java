package io.github.factoryfx.javafx.widget.factorydiff;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FactoryDiffWidget<R extends FactoryBase<?, R>> implements Widget {
    private final UniformDesign uniformDesign;
    private final LanguageText columnFactory=new LanguageText().en("data").de("Objekt");
    private final LanguageText columnField=new LanguageText().en("field").de("Feld");
    private final LanguageText columnPrevious=new LanguageText().en("previous").de("Alt");
    private final LanguageText columnNew=new LanguageText().en("new").de("Neu");
    private final LanguageText titlePrevious=new LanguageText().en("previous value ").de("Alter Wert");
    private final LanguageText titleNew=new LanguageText().en("new value").de("Neuer Wert");
    private final LanguageText noChangesFound=new LanguageText().en("No changes found").de("keine Änderungen gefunden");

    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;

    public FactoryDiffWidget(UniformDesign uniformDesign, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder){
        this.uniformDesign=uniformDesign;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
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

        SplitPane diffValuesPane = new SplitPane();
        final Node previousNode = addTitle(previousValueDisplay, uniformDesign.getText(titlePrevious));
        diffValuesPane.getItems().add(previousNode);



        final Node newNode = addTitle(newValueDisplay, uniformDesign.getText(titleNew));
        diffValuesPane.getItems().add(newNode);



        VBox vBox=new VBox();
        VBox.setMargin(diffValuesPane,new Insets(0,3,0,3));
        VBox.setVgrow(diffValuesPane,Priority.ALWAYS);
        vBox.getChildren().add(diffValuesPane);
        verticalSplitPane.getItems().add(vBox);

        diffTableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            AttributeDiffInfoExtended selectedItem = diffTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                FactoryBase<?,?> previousRoot = selectedItem.previousRoot;
                FactoryBase<?,?> newRoot = selectedItem.newRoot;
                AttributeDiffInfo diffItem = selectedItem.attributeDiffInfo;

                Attribute<?,?> previousAttribute = diffItem.getAttribute(previousRoot);
                if (previousAttribute!=null) {
                    previousValueDisplay.setCenter(createEditor(previousAttribute,diffItem.getFactory(previousRoot).internal().getAttributeMetadata(previousAttribute)));
                } else {
                    previousValueDisplay.setCenter(null);
                }

                Attribute<?,?> newAttribute = diffItem.getAttribute(newRoot);
                if (newAttribute!=null) {
                    newValueDisplay.setCenter(createEditor(newAttribute,diffItem.getFactory(newRoot).internal().getAttributeMetadata(newAttribute)));
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

    private Node createEditor(Attribute<?,?> attribute, AttributeMetadata attributeMetadata){
        final AttributeVisualisation previousAttributeEditor = attributeVisualisationMappingBuilder.getAttributeVisualisation(attribute,attributeMetadata, null, null);
        previousAttributeEditor.expand();
        return previousAttributeEditor.createReadOnlyVisualisation();
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
        label.setPadding(new Insets(3));
        vBox.getChildren().add(label);
        VBox.setVgrow(node, Priority.ALWAYS);
        vBox.getChildren().add(node);
        return vBox;
    }
    public void updateMergeDiff(MergeDiffInfo<R> mergeDiff) {
        FactoryBase<?,?> previousRoot=mergeDiff.getPreviousRootData();
        FactoryBase<?,?> newRoot=mergeDiff.getNewRootData();
        diff = new ArrayList<>();
        diff.addAll(mergeDiff.mergeInfos.stream().map(info->new AttributeDiffInfoExtended(true,false,false, info,previousRoot,newRoot)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.conflictInfos.stream().map(info->new AttributeDiffInfoExtended(false,true,false, info,previousRoot,newRoot)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.permissionViolations.stream().map(info->new AttributeDiffInfoExtended(false,false,true, info,previousRoot,newRoot)).collect(Collectors.toList()));
        if (diffListUpdater!=null){
            diffListUpdater.accept(diff);
        }
    }

}
