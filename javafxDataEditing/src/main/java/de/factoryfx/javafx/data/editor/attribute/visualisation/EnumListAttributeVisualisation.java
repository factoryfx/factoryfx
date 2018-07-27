package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.CheckComboBoxHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class EnumListAttributeVisualisation extends ListAttributeEditorVisualisation<Enum<?>> {

    private final Collection<Enum<?>> possibleEnumConstants;
    private final StringConverter<Enum<?>> stringConverter;
    private final EnumListAttribute<?> enumListAttribute;

    public EnumListAttributeVisualisation(Collection<Enum<?>> possibleEnumConstants, StringConverter<Enum<?>> stringConverter, EnumListAttribute<?> enumListAttribute) {
        this.possibleEnumConstants = possibleEnumConstants;
        this.stringConverter = stringConverter;
        this.enumListAttribute = enumListAttribute;
    }

    @SuppressWarnings("unchecked")
    private void updateCheckComboBox(CheckComboBox<Enum<?>> comboBox){
        comboBox.getItems().clear();

        List<Enum<?>> items = new ArrayList<>(possibleEnumConstants);
        comboBox.getItems().addAll(items);

        comboBox.getItems().forEach(i->{
            if (enumListAttribute.contains(i)) {
                comboBox.getItemBooleanProperty(i).set(true);
            }
            comboBox.getItemBooleanProperty(i).addListener((a,b,newV)->{
                if (newV) {
                    boolean contained = enumListAttribute.contains(i);
                    if (!contained) {
                        ((List<Enum<?>>)enumListAttribute).add(i);
                    }
                } else {
                    enumListAttribute.get().remove(i);
                }
            });
        });
        enumListAttribute.get().removeIf(d->!items.contains(d));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent(ObservableList<Enum<?>> readOnlyList, Consumer<Consumer<List<Enum<?>>>> listModifyingAction, boolean readonly) {
        CheckComboBox<Enum<?>> comboBox = new CheckComboBox<>();
        updateCheckComboBox(comboBox);

        CheckComboBoxHelper.addOpenCloseListener(comboBox,()->updateCheckComboBox(comboBox));

        comboBox.setConverter(stringConverter);
        comboBox.setMinWidth(300);

        final MenuItem selectAll = new MenuItem("alle auswählen");
        selectAll.setOnAction(event -> {
            enumListAttribute.get().clear();
            enumListAttribute.get().addAll((List)comboBox.getItems());
            updateCheckComboBox(comboBox);
        });
        final MenuItem unSelectAll = new MenuItem("keine auswählen");
        unSelectAll.setOnAction(event -> {
            enumListAttribute.get().clear();
            updateCheckComboBox(comboBox);
        });
        comboBox.setContextMenu(new ContextMenu(selectAll, unSelectAll));

        comboBox.setDisable(readonly);
        return comboBox;
    }
}
