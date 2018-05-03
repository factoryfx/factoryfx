package de.factoryfx.javafx.data.editor.attribute.visualisation;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

import org.controlsfx.control.CheckComboBox;

import impl.org.controlsfx.skin.CheckComboBoxSkin;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;

public class CatalogListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final ReferenceListAttribute<Data,?> referenceListAttribute;


    public CatalogListAttributeVisualisation(Supplier<Collection<? extends Data>> possibleValuesProvider, ReferenceListAttribute<Data, ?> referenceListAttribute) {
        this.possibleValuesProvider = possibleValuesProvider;
        this.referenceListAttribute = referenceListAttribute;
    }

    @Override
    public Node createContent(ObservableList<Data> readOnlyList, Consumer<Consumer<List<Data>>> listModifyingAction, boolean readonly) {

        CheckComboBox<Data> comboBox = new CheckComboBox<>();
        updateCheckComboBox(comboBox);

        //workaround: http://stackoverflow.com/questions/25177523/how-to-listen-to-open-close-events-of-a-checkcombobox
        comboBox.skinProperty().addListener((ChangeListener<Skin>) (skinObs, oldVal, newVal) -> {
            if (oldVal == null && newVal != null) {
                CheckComboBoxSkin skin = (CheckComboBoxSkin) newVal;
                ComboBox combo = (ComboBox) skin.getChildren().get(0);
                combo.showingProperty().addListener((obs, hidden, showing) -> updateCheckComboBox(comboBox));
            }
        });
        comboBox.addEventHandler(ComboBox.ON_HIDDEN, event -> {
            System.out.println("CheckComboBox is now hidden.");
        });

        comboBox.setConverter(new StringConverter<Data>() {
            @Override
            public String toString(Data object) {
                return object.internal().getDisplayText();
            }

            @Override
            public Data fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        comboBox.setMinWidth(300);

        final MenuItem selectAll = new MenuItem("alle auswählen");
        selectAll.setOnAction(event -> {
            referenceListAttribute.get().clear();
            referenceListAttribute.get().addAll(comboBox.getItems());
            updateCheckComboBox(comboBox);
        });
        final MenuItem unSelectAll = new MenuItem("keine auswählen");
        unSelectAll.setOnAction(event -> {
            referenceListAttribute.get().clear();
            updateCheckComboBox(comboBox);
        });
        comboBox.setContextMenu(new ContextMenu(selectAll, unSelectAll));

        comboBox.setDisable(readonly);
        return comboBox;
    }

    private void updateCheckComboBox(CheckComboBox<Data> comboBox){
        comboBox.getItems().clear();

        List<Data> items = new ArrayList<>(possibleValuesProvider.get());
        comboBox.getItems().addAll(items);
        //datas.stream().filter(unlisted->!items.contains(unlisted)).forEach(comboBox.getItems()::add);

        comboBox.getItems().forEach(i->{
            if (referenceListAttribute.get().contains(i)) {
                comboBox.getItemBooleanProperty(i).set(true);
            }
            comboBox.getItemBooleanProperty(i).addListener((a,b,newV)->{
                if (newV) {
                    boolean contained = referenceListAttribute.get().contains(i);
                    if (!contained) {
                        referenceListAttribute.get().add(i);
                    }
                } else {
                    referenceListAttribute.get().remove(i);
                }
            });
        });
        referenceListAttribute.get().removeIf(d->!items.contains(d));
    }
}
