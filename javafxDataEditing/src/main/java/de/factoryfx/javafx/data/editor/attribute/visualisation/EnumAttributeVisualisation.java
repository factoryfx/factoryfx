package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.List;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;

public class EnumAttributeVisualisation extends ValueAttributeEditorVisualisation<Enum<?>> {
    private final List<? extends Enum<?>> possibleEnumConstants;
    private final UniformDesign uniformDesign;
    private final StringConverter<Enum<?>> stringConverter;

    public EnumAttributeVisualisation(UniformDesign uniformDesign, List<? extends Enum<?>> possibleEnumConstants, StringConverter<Enum<?>> stringConverter) {
        this.possibleEnumConstants = possibleEnumConstants;
        this.uniformDesign = uniformDesign;
        this.stringConverter = stringConverter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createVisualisation(SimpleObjectProperty<Enum<?>> boundTo, boolean readonly) {
        ComboBox<Enum<?>> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(possibleEnumConstants);
        comboBox.valueProperty().bindBidirectional(boundTo);
        comboBox.setConverter(stringConverter);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton, FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> boundTo.set(null));
        deleteButton.disableProperty().bind(boundTo.isNull().or(Bindings.createBooleanBinding(()->readonly)));

        HBox hbox = new HBox(3);
        hbox.getChildren().addAll(comboBox,deleteButton);
        hbox.setDisable(readonly);
        return hbox;
    }
}
