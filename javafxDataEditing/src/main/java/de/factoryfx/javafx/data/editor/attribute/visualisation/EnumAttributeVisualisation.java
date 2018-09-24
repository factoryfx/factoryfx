package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.List;

import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;

public class EnumAttributeVisualisation<E extends Enum<E>> extends ValueAttributeVisualisation<E, EnumAttribute<E>> {
    private final List<E> possibleEnumConstants;
    private final UniformDesign uniformDesign;
    private final StringConverter<E> stringConverter;

    public EnumAttributeVisualisation(EnumAttribute<E> attribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(attribute,validationDecoration);
        this.possibleEnumConstants = attribute.internal_possibleEnumValues();
        this.uniformDesign = uniformDesign;
        this.stringConverter = new StringConverter<>() {
            @Override
            public String toString(E enumValue) {
                if (enumValue==null){
                    return attribute.internal_enumDisplayText(null, uniformDesign::getText);
                }
                return attribute.internal_enumDisplayText(enumValue, uniformDesign::getText);
            }
            @Override
            public E fromString(String string) { return null;} //nothing
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createValueVisualisation() {
        ComboBox<E> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(possibleEnumConstants);
        comboBox.valueProperty().bindBidirectional(observableAttributeValue);
        comboBox.setConverter(stringConverter);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton, FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> observableAttributeValue.set(null));
        deleteButton.disableProperty().bind(observableAttributeValue.isNull());//.or(Bindings.createBooleanBinding(()-> isReadOnlyVisualisation)));

        HBox hbox = new HBox(3);
        hbox.getChildren().addAll(comboBox,deleteButton);
        hbox.disableProperty().bind(readOnly);
        return hbox;
    }
}
