package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;
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

    @SuppressWarnings("unchecked")
    public EnumAttributeVisualisation(EnumAttribute<E> attribute, AttributeMetadata attributeMetadata, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(attribute,validationDecoration);
        this.possibleEnumConstants = Arrays.stream(attributeMetadata.enumClass.getEnumConstants()).map(e->(E)e).collect(Collectors.toList());
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
