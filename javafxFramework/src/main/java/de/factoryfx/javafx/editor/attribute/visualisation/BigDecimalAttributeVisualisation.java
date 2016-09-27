package de.factoryfx.javafx.editor.attribute.visualisation;

import java.math.BigDecimal;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.BigDecimalStringConverter;

public class BigDecimalAttributeVisualisation implements AttributeEditorVisualisation<BigDecimal> {

    @Override
    public Node createContent(SimpleObjectProperty<BigDecimal> boundTo, Attribute<BigDecimal> attribute) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupBigDecimalLongTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new BigDecimalStringConverter());
        return textField;
    }
}
