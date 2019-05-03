package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import io.github.factoryfx.factory.attribute.types.BigDecimalAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.converter.BigDecimalStringConverter;
import io.github.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class BigDecimalAttributeVisualisation extends ValueAttributeVisualisation<BigDecimal,BigDecimalAttribute> {

    public final String decimalFormatPattern;

    public BigDecimalAttributeVisualisation(BigDecimalAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
        this.decimalFormatPattern = attribute.internal_getDecimalFormatPattern();
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupBigDecimalTextField(textField,decimalFormatPattern);



        DecimalFormat decimalFormat = new DecimalFormat(decimalFormatPattern);
        decimalFormat.setParseBigDecimal(true);
        textField.textProperty().bindBidirectional(observableAttributeValue, new BigDecimalStringConverter(decimalFormat));
        textField.disableProperty().bind(readOnly);
        return textField;
    }

}
