package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
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

public class EnumListAttributeVisualisation extends ListAttributeEditorVisualisation<EnumListAttribute.EnumWrapper<?>> {

    private final Collection<EnumListAttribute.EnumWrapper<?>> possibleEnumConstants;
    private final StringConverter<EnumListAttribute.EnumWrapper<?>> stringConverter;
    private final EnumListAttribute<?> enumListAttribute;

    public EnumListAttributeVisualisation(Collection<EnumListAttribute.EnumWrapper<?>> possibleEnumConstants, StringConverter<EnumListAttribute.EnumWrapper<?>> stringConverter, EnumListAttribute<?> enumListAttribute) {
        this.possibleEnumConstants = possibleEnumConstants;
        this.stringConverter = stringConverter;
        this.enumListAttribute = enumListAttribute;
    }


    private void updateCheckComboBox(CheckComboBox<EnumListAttribute.EnumWrapper<?>> comboBox){
        comboBox.getItems().clear();

        List<EnumListAttribute.EnumWrapper<?>> items = new ArrayList<EnumListAttribute.EnumWrapper<?>>(possibleEnumConstants);
        comboBox.getItems().addAll(items);

        comboBox.getItems().forEach(i->{
            if (enumListAttribute.get().contains(i)) {
                comboBox.getItemBooleanProperty(i).set(true);
            }
            comboBox.getItemBooleanProperty(i).addListener((a,b,newV)->{
                if (newV) {
                    boolean contained = enumListAttribute.get().contains(i);
                    if (!contained) {
                        enumListAttribute.get().add((EnumListAttribute.EnumWrapper)i);
                    }
                } else {
                    enumListAttribute.get().remove(i);
                }
            });
        });
        enumListAttribute.get().removeIf(d->!items.contains(d));
    }

    @Override
    public Node createContent(ObservableList<EnumListAttribute.EnumWrapper<?>> readOnlyList, Consumer<Consumer<List<EnumListAttribute.EnumWrapper<?>>>> listModifyingAction, boolean readonly) {
        CheckComboBox<EnumListAttribute.EnumWrapper<?>> comboBox = new CheckComboBox<>();
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
