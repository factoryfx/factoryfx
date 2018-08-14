package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.controlsfx.control.CheckComboBox;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.editor.attribute.converter.DataStringConverter;
import de.factoryfx.javafx.data.util.CheckComboBoxHelper;

public class CatalogListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final ReferenceListAttribute<Data, ?> referenceListAttribute;

    public CatalogListAttributeVisualisation(Supplier<Collection<? extends Data>> possibleValuesProvider, ReferenceListAttribute<Data, ?> referenceListAttribute) {
        this.possibleValuesProvider = possibleValuesProvider;
        this.referenceListAttribute = referenceListAttribute;
    }

    @Override
    public Node createContent(ObservableList<Data> readOnlyList, Consumer<Consumer<List<Data>>> listModifyingAction, boolean readonly) {
        CheckComboBox<Data> comboBox = new CheckComboBox<>();
        possibleValuesProvider.get().stream().distinct().forEach(comboBox.getItems()::add);
        CheckComboBoxHelper.addOpenCloseListener(comboBox, this::updateCheckComboBox);

        comboBox.setConverter(new DataStringConverter());
        comboBox.setMinWidth(300);

        final MenuItem selectAll = new MenuItem("alle auswählen");
        selectAll.setOnAction(event -> {
            setAll(comboBox, true);
            updateCheckComboBox(comboBox);
        });
        final MenuItem unSelectAll = new MenuItem("keine auswählen");
        unSelectAll.setOnAction(event -> {
            setAll(comboBox, false);
            updateCheckComboBox(comboBox);
        });
        comboBox.setContextMenu(new ContextMenu(selectAll, unSelectAll));

        updateCheckComboBox(comboBox);
        return comboBox;
    }

    void setAll(CheckComboBox<Data> comboBox, boolean value) {
        comboBox.getItems().stream().map(comboBox::getItemBooleanProperty).forEach(s -> s.setValue(value));
    }

    private void updateCheckComboBox(CheckComboBox<Data> comboBox) {
        comboBox.getItems().forEach(data -> {
            if (referenceListAttribute.get().contains(data)) {
                comboBox.getItemBooleanProperty(data).set(true);
            }
            comboBox.getItemBooleanProperty(data).addListener((a, b, newV) -> {
                if (newV) {
                    if (!referenceListAttribute.get().contains(data)) {
                        referenceListAttribute.get().add(data);
                    }
                } else {
                    referenceListAttribute.get().remove(data);
                }
            });

        });
    }
}
