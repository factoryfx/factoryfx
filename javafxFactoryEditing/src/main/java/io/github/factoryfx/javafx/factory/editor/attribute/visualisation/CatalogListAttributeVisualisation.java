package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import java.util.Collection;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ListAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.controlsfx.control.CheckComboBox;

import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListBaseAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.factory.editor.attribute.converter.DataStringConverter;
import io.github.factoryfx.javafx.factory.util.CheckComboBoxHelper;
import io.github.factoryfx.javafx.factory.util.UniformDesign;

public class CatalogListAttributeVisualisation<T extends FactoryBase<?,?>, A extends FactoryReferenceListBaseAttribute<?,?,T,A>> extends ListAttributeVisualisation<T,A> {
    private final UniformDesign uniformDesign;
    private final Supplier<Collection<T>> possibleValuesProvider;
    private final A referenceListAttribute;

    public CatalogListAttributeVisualisation(A referenceListAttribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(referenceListAttribute,validationDecoration);
        this.uniformDesign = uniformDesign;


        this.possibleValuesProvider = referenceListAttribute::internal_possibleValues;
        this.referenceListAttribute = referenceListAttribute;
    }

    private final static LanguageText selectAll = new LanguageText().en("Select all").de("Alle auswählen");
    private final static LanguageText selectNon = new LanguageText().en("Deselect all").de("Keins auswählen");

    @Override
    public Node createValueListVisualisation() {
        CheckComboBox<T> comboBox = new CheckComboBox<>();
        possibleValuesProvider.get().stream().distinct().forEach(comboBox.getItems()::add);
        CheckComboBoxHelper.addOpenCloseListener(comboBox, this::updateCheckComboBox);

        comboBox.setConverter(new DataStringConverter<>());
        comboBox.setMinWidth(300);

        comboBox.disableProperty().bind(readOnly);
        ContextMenu contextMenu = new ContextMenu(menuItem(selectAll, comboBox, true), menuItem(selectNon, comboBox, false));
        contextMenu.getItems().forEach(m->m.disableProperty().bind(readOnly));
        comboBox.setContextMenu(contextMenu);


        updateCheckComboBox(comboBox);
        return comboBox;
    }

    private MenuItem menuItem(LanguageText text, CheckComboBox<T> comboBox, boolean value) {
        final MenuItem menuItem = new MenuItem(uniformDesign.getText(text));
        menuItem.setOnAction(event -> {
            comboBox.getItems().stream().map(comboBox::getItemBooleanProperty).forEach(s -> s.setValue(value));
            updateCheckComboBox(comboBox);
        });
        return menuItem;
    }

    private void updateCheckComboBox(CheckComboBox<T> comboBox) {
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
