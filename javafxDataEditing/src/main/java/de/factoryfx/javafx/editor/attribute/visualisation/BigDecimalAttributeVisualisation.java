package de.factoryfx.javafx.editor.attribute.visualisation;

import java.math.BigDecimal;

import de.factoryfx.javafx.editor.attribute.ImmutableAttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.BigDecimalStringConverter;

public class BigDecimalAttributeVisualisation extends ImmutableAttributeEditorVisualisation<BigDecimal> {

    @Override
    public Node createContent(SimpleObjectProperty<BigDecimal> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupBigDecimalLongTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new BigDecimalStringConverter());
        return textField;
    }
}
