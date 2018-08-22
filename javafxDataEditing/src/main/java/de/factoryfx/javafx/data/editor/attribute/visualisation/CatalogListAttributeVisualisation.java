package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.Collection;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.controlsfx.control.CheckComboBox;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.editor.attribute.converter.DataStringConverter;
import de.factoryfx.javafx.data.util.CheckComboBoxHelper;
import de.factoryfx.javafx.data.util.UniformDesign;

public class CatalogListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {
    private final UniformDesign uniformDesign;
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final ReferenceListAttribute<Data, ?> referenceListAttribute;

    public CatalogListAttributeVisualisation(UniformDesign uniformDesign, Supplier<Collection<? extends Data>> possibleValuesProvider, ReferenceListAttribute<Data, ?> referenceListAttribute) {
        this.uniformDesign = uniformDesign;
        this.possibleValuesProvider = possibleValuesProvider;
        this.referenceListAttribute = referenceListAttribute;
    }

    private final static LanguageText selectAll = new LanguageText().en("Select all").de("Alle auswählen");
    private final static LanguageText selectNon = new LanguageText().en("Deselect all").de("Keins auswählen");

    @Override
    public Node createContent(ObservableList<Data> readOnlyList, boolean readonly) {
        CheckComboBox<Data> comboBox = new CheckComboBox<>();
        possibleValuesProvider.get().stream().distinct().forEach(comboBox.getItems()::add);
        CheckComboBoxHelper.addOpenCloseListener(comboBox, this::updateCheckComboBox);

        comboBox.setConverter(new DataStringConverter());
        comboBox.setMinWidth(300);

        comboBox.setContextMenu(new ContextMenu(menuItem(selectAll, comboBox, true), menuItem(selectNon, comboBox, false)));

        updateCheckComboBox(comboBox);
        return comboBox;
    }

    private MenuItem menuItem(LanguageText text, CheckComboBox<Data> comboBox, boolean value) {
        final MenuItem menuItem = new MenuItem(uniformDesign.getText(text));
        menuItem.setOnAction(event -> {
            comboBox.getItems().stream().map(comboBox::getItemBooleanProperty).forEach(s -> s.setValue(value));
            updateCheckComboBox(comboBox);
        });
        return menuItem;
    }

    private void updateCheckComboBox(CheckComboBox<Data> comboBox) {
        comboBox.getItems().forEach(data -> {
            if (referenceListAttribute.contains(data)) {
                comboBox.getItemBooleanProperty(data).set(true);
            }
            comboBox.getItemBooleanProperty(data).addListener((a, b, newV) -> {
                if (newV) {
                    if (!referenceListAttribute.contains(data)) {
                        referenceListAttribute.add(data);
                    }
                } else {
                    referenceListAttribute.remove(data);
                }
            });

        });
    }
}
