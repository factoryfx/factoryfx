package io.github.factoryfx.javafx.editor.attribute.visualisation;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.attribute.types.ChoiceAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;

public class ChoiceAttributeVisualisation extends ValueAttributeVisualisation<String, ChoiceAttribute> {
    private final ChoiceAttribute boundAttribute;
    private final UniformDesign uniformDesign;

    public ChoiceAttributeVisualisation(ChoiceAttribute boundAttribute, UniformDesign uniformDesign) {
        super(boundAttribute, new ValidationDecoration(uniformDesign));
        this.boundAttribute = boundAttribute;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createValueVisualisation() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(this.boundAttribute.getPossibleValues());
        comboBox.valueProperty().bindBidirectional(this.observableAttributeValue);
        HBox hbox = new HBox(3.0);
        hbox.getChildren().add(comboBox);
        if(!boundAttribute.internal_required()) {
            Button deleteButton = new Button();
            this.uniformDesign.addDangerIcon(deleteButton, FontAwesome.Glyph.TIMES);
            deleteButton.setOnAction((event) -> {
                this.observableAttributeValue.set(null);
            });
            deleteButton.disableProperty().bind(this.observableAttributeValue.isNull());
            hbox.getChildren().add(deleteButton);
        }
        hbox.disableProperty().bind(this.readOnly);
        return hbox;
    }
}
