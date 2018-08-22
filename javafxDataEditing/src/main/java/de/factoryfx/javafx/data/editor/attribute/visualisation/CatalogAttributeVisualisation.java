package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.Collection;
import java.util.function.Supplier;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;

public class CatalogAttributeVisualisation extends ValueAttributeEditorVisualisation<Data> {
    private final Supplier<Collection<Data>> possibleValuesProvider;

    public CatalogAttributeVisualisation(Supplier<Collection<Data>> possibleValuesProvider) {
        this.possibleValuesProvider = possibleValuesProvider;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<Data> boundTo, boolean readonly) {

        ComboBox<Data> comboBox = new ComboBox<>();
        comboBox.getItems().add(0, null);
        comboBox.getItems().addAll(possibleValuesProvider.get());
        comboBox.valueProperty().bindBidirectional(boundTo);

        comboBox.setEditable(false);

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Data object) {
                return object == null ? "" : object.internal().getDisplayText();
            }

            @Override
            public Data fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        comboBox.setMinWidth(300);
        comboBox.setDisable(readonly);
        return comboBox;
    }

}
