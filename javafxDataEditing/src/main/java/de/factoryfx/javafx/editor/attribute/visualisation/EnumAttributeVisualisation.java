package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collection;

import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;

public class EnumAttributeVisualisation extends ValueAttributeEditorVisualisation<EnumAttribute.EnumWrapper> {
    private final Collection<EnumAttribute.EnumWrapper> possibleEnumConstants;
    private final UniformDesign uniformDesign;

    public EnumAttributeVisualisation(UniformDesign uniformDesign, Collection<EnumAttribute.EnumWrapper> possibleEnumConstants) {
        this.possibleEnumConstants = possibleEnumConstants;
        this.uniformDesign = uniformDesign;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createVisualisation(SimpleObjectProperty<EnumAttribute.EnumWrapper> boundTo, boolean readonly) {
        ComboBox<EnumAttribute.EnumWrapper> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(possibleEnumConstants);
        comboBox.valueProperty().bindBidirectional(boundTo);


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
