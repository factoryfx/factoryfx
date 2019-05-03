package io.github.factoryfx.javafx.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ListAttributeVisualisation;
import io.github.factoryfx.javafx.util.CheckComboBoxHelper;
import io.github.factoryfx.javafx.util.UniformDesign;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumListAttributeVisualisation<E extends Enum<E>> extends ListAttributeVisualisation<E, EnumListAttribute<E>> {

    private final Collection<E> possibleEnumConstants;
    private final StringConverter<E> stringConverter;
    private final EnumListAttribute<E> enumListAttribute;

    public EnumListAttributeVisualisation(EnumListAttribute<E> enumListAttribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(enumListAttribute,validationDecoration);
        this.enumListAttribute = enumListAttribute;
        this.possibleEnumConstants = enumListAttribute.internal_possibleEnumValues();
        this.stringConverter = new StringConverter<E>() {
            @Override
            public String toString(E enumValue) {
                if (enumValue==null){
                    return enumListAttribute.internal_enumDisplayText(null, uniformDesign::getText);
                }
                return enumListAttribute.internal_enumDisplayText(enumValue, uniformDesign::getText);
            }
            @Override
            public E fromString(String string) { return null;} //nothing
        };
    }

    @SuppressWarnings("unchecked")
    private void updateCheckComboBox(CheckComboBox<E> comboBox){
        comboBox.getItems().clear();

        List<E> items = new ArrayList<>(possibleEnumConstants);
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
    public Node createValueListVisualisation() {
        CheckComboBox<E> comboBox = new CheckComboBox<>();
        updateCheckComboBox(comboBox);

        CheckComboBoxHelper.addOpenCloseListener(comboBox, this::updateCheckComboBox);

        comboBox.setConverter(stringConverter);
        comboBox.setMinWidth(300);

        final MenuItem selectAll = new MenuItem("alle auswählen");
        selectAll.setOnAction(event -> {
            enumListAttribute.get().clear();
            enumListAttribute.get().addAll(comboBox.getItems());
            updateCheckComboBox(comboBox);
        });
        final MenuItem unSelectAll = new MenuItem("keine auswählen");
        unSelectAll.setOnAction(event -> {
            enumListAttribute.get().clear();
            updateCheckComboBox(comboBox);
        });
        comboBox.setContextMenu(new ContextMenu(selectAll, unSelectAll));

        comboBox.disableProperty().bind(readOnly);
        return comboBox;
    }
}
