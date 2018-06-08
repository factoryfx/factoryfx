package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import impl.org.controlsfx.skin.CheckComboBoxSkin;
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

        //workaround: http://stackoverflow.com/questions/25177523/how-to-listen-to-open-close-events-of-a-checkcombobox
        comboBox.skinProperty().addListener((ChangeListener<Skin>) (skinObs, oldVal, newVal) -> {
            if (oldVal == null && newVal != null) {
                CheckComboBoxSkin skin = (CheckComboBoxSkin) newVal;
                ComboBox combo = (ComboBox) skin.getChildren().get(0);
                combo.showingProperty().addListener((obs, hidden, showing) -> updateCheckComboBox(comboBox));
            }
        });

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
