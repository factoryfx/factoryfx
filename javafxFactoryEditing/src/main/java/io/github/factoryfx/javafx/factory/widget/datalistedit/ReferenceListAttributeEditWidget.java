package io.github.factoryfx.javafx.factory.widget.datalistedit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListBaseAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.select.SelectDataDialog;

/**
 * Data list edit widget (add Button,delete Button,... )
 * @param <T> data
 */
public class ReferenceListAttributeEditWidget<RS extends FactoryBase<?,RS>,L, T extends FactoryBase<L,RS>> implements Widget {

    private LanguageText editText= new LanguageText().en("edit").de("Editieren");
    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText copyText= new LanguageText().en("copy").de("Kopieren");

    private LanguageText deleteConfirmationTitle= new LanguageText().en("Delete").de("Löschen");
    private LanguageText deleteConfirmationHeader= new LanguageText().en("delete item").de("Eintrag löschen");
    private LanguageText deleteConfirmationContent= new LanguageText().en("delete item?").de("Soll der Eintrag gelöscht werden?");


    private final UniformDesign uniformDesign;
    private final Supplier<List<? extends T>> newValueProvider;
    private final Supplier<Collection<T>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;
    private final FactoryReferenceListBaseAttribute<RS,L,T,?> factoryReferenceListBaseAttribute;
    private final TableView<T> tableView;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final BooleanBinding multipleItemsSelected;
    private final boolean isUserCreateable;
    private final BiConsumer<T,List<T>> deleter;

    public ReferenceListAttributeEditWidget(FactoryReferenceListBaseAttribute<RS,L,T,?> factoryReferenceListBaseAttribute, TableView<T> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, Supplier<List<? extends T>> newValueProvider, Supplier<Collection<T>> possibleValuesProvider, BiConsumer<T,List<T>> deleter , boolean isUserEditable, boolean isUserSelectable, boolean isUserCreateable) {
        this.uniformDesign = uniformDesign;
        this.newValueProvider = newValueProvider;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
        this.factoryReferenceListBaseAttribute = factoryReferenceListBaseAttribute;
        this.tableView = tableView;
        this.navigateToData =  navigateToData;
        multipleItemsSelected = Bindings.createBooleanBinding(() -> tableView.getSelectionModel().getSelectedItems().size() > 1, tableView.getSelectionModel().getSelectedItems());
        this.isUserCreateable = isUserCreateable;
        this.deleter=deleter;
    }

    public ReferenceListAttributeEditWidget(TableView<T> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, FactoryReferenceListBaseAttribute<RS,L,T,?> factoryReferenceListBaseAttribute) {
        this(factoryReferenceListBaseAttribute, tableView, navigateToData, uniformDesign,
                factoryReferenceListBaseAttribute::internal_createNewPossibleValues, factoryReferenceListBaseAttribute::internal_possibleValues, (t, ts) -> factoryReferenceListBaseAttribute.internal_deleteFactory(t),
                !factoryReferenceListBaseAttribute.internal_isUserReadOnly(), factoryReferenceListBaseAttribute.internal_isUserSelectable(), factoryReferenceListBaseAttribute.internal_isUserCreatable());
    }

    @Override
    public HBox createContent() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));

        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
            new SelectDataDialog<T>(possibleValuesProvider.get(),uniformDesign).show(selectButton.getScene().getWindow(), data -> factoryReferenceListBaseAttribute.get().add(data));
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable);

        Button adderButton = new Button();
        uniformDesign.addIcon(adderButton,FontAwesome.Glyph.PLUS);
        adderButton.setOnAction(event -> {
            addNewReference(adderButton.getScene().getWindow());
        });
        adderButton.setDisable(!isUserEditable || !isUserCreateable);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> deleteSelected(deleteButton.getScene().getWindow()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(new SimpleBooleanProperty(!isUserEditable)));

        Button moveUpButton = new Button();
        uniformDesign.addIcon(moveUpButton,FontAwesome.Glyph.ANGLE_UP);
        moveUpButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveUpButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex -1>=0){
                Collections.swap(factoryReferenceListBaseAttribute, selectedIndex, selectedIndex -1);
                tableView.getSelectionModel().select(selectedIndex -1);
            }
        });
        Button moveDownButton = new Button();
        uniformDesign.addIcon(moveDownButton,FontAwesome.Glyph.ANGLE_DOWN);
        moveDownButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveDownButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex+1< factoryReferenceListBaseAttribute.size()){
                Collections.swap(factoryReferenceListBaseAttribute, selectedIndex, selectedIndex +1);
                tableView.getSelectionModel().select(selectedIndex +1);
            }
        });
        Button sortButton = new Button();
        uniformDesign.addIcon(sortButton,FontAwesome.Glyph.SORT_ALPHA_ASC);
        sortButton.setOnAction(event -> {
            factoryReferenceListBaseAttribute.sort((d1, d2)->{
                String s1 = Optional.ofNullable(d1.internal().getDisplayText()).orElse("");
                String s2 = Optional.ofNullable(d2.internal().getDisplayText()).orElse("");
                return s1.compareTo(s2);
            });
        });
        sortButton.disableProperty().bind(new SimpleBooleanProperty(!isUserEditable));

        Button copyButton = new Button();
        uniformDesign.addIcon(copyButton,FontAwesome.Glyph.COPY);
        copyButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected).or(new SimpleBooleanProperty(!isUserEditable)));
        copyButton.setOnAction(event -> {
            T copy = tableView.getSelectionModel().getSelectedItem().utility().semanticCopy();
            factoryReferenceListBaseAttribute.add(copy);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(copy);
        });

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setSpacing(3);
        buttons.getChildren().add(showButton);
        buttons.getChildren().add(selectButton);
        buttons.getChildren().add(adderButton);
        buttons.getChildren().add(copyButton);
        buttons.getChildren().add(deleteButton);
        buttons.getChildren().add(moveUpButton);
        buttons.getChildren().add(moveDownButton);
        buttons.getChildren().add(sortButton);

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        selectButton.setTooltip(new Tooltip(uniformDesign.getText(selectText)));
        adderButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));
        copyButton.setTooltip(new Tooltip(uniformDesign.getText(copyText)));

        HBox.setMargin(moveUpButton,new Insets(0,0,0,9));
        HBox.setMargin(moveDownButton,new Insets(0,9,0,0));


        tableView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode()== KeyCode.DELETE){
                deleteSelected(tableView.getScene().getWindow());
            }
            if (event.getCode()== KeyCode.ENTER){
                navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
            }
        });
        return buttons;
    }

    private void deleteSelected(Window owner) {
        if (uniformDesign.isAskBeforeDelete()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(owner);
            alert.setTitle(uniformDesign.getText(deleteConfirmationTitle));
            alert.setHeaderText(uniformDesign.getText(deleteConfirmationHeader));
            alert.setContentText(uniformDesign.getText(deleteConfirmationContent));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()){
                if (result.get() != ButtonType.OK){
                    return;
                }
            }
        }

        final List<T> selectedItems = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        selectedItems.forEach(t -> deleter.accept(t, factoryReferenceListBaseAttribute));
    }

    private void addNewReference(Window owner) {
        List<? extends T> newDataList = newValueProvider.get();
        if (!newDataList.isEmpty()){
            if (newDataList.size()==1){
                factoryReferenceListBaseAttribute.add(newDataList.get(0));
                navigateToData.accept(newDataList.get(0));
            } else {
                List<T> newDataListData = new ArrayList<>(newDataList);
                new SelectDataDialog<T>(newDataListData,uniformDesign).show(owner, data -> {
                    factoryReferenceListBaseAttribute.add(data);
                    navigateToData.accept(data);
                });
            }
        }


    }

}
