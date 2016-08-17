package de.factoryfx.adminui.javafx.framework.editor.editors;

import de.factoryfx.factory.attribute.util.EnumAttribute;
import de.factoryfx.adminui.javafx.framework.editor.AttributeEditor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class EnumEditor<E extends Enum<E>> extends AttributeEditor<E,EnumAttribute<E>> {

    private final Class<E> enumClazz;
    public EnumEditor(Class<E> enumClazz) {
        super(enumClazz);
        this.enumClazz = enumClazz;
    }

    @Override
    public Node createContent() {
        ComboBox<E> comboBox = new ComboBox<>();
        comboBox.disableProperty().bind(disabledProperty());
        comboBox.setEditable(false);
        comboBox.getItems().addAll(enumClazz.getEnumConstants());
        comboBox.valueProperty().bindBidirectional(boundTo);
        return comboBox;
    }

}