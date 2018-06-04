package de.factoryfx.javafx.data.editor.attribute.visualisation;


import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import impl.org.controlsfx.skin.CheckComboBoxSkin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CatalogAttributeVisualisation extends ValueAttributeEditorVisualisation<Data> {
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final ReferenceAttribute<Data,?> referenceAttribute;


    public CatalogAttributeVisualisation(Supplier<Collection<? extends Data>> possibleValuesProvider, ReferenceAttribute<Data, ?> referenceAttribute) {
        this.possibleValuesProvider = possibleValuesProvider;
        this.referenceAttribute = referenceAttribute;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<Data> boundTo, boolean readonly) {

        ComboBox<Data> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        updateCheckComboBox(comboBox, boundTo);

        comboBox.setConverter(new StringConverter<Data>() {
            @Override
            public String toString(Data object) {
                return object==null?"":object.internal().getDisplayText();
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

    private void updateCheckComboBox(ComboBox<Data> comboBox, SimpleObjectProperty<Data> boundTo){
        comboBox.getItems().clear();
        List<Data> items = new ArrayList<>(possibleValuesProvider.get());
        items.add(0,null);
        comboBox.getItems().addAll(items);
        comboBox.valueProperty().bindBidirectional(boundTo);

    }
}
