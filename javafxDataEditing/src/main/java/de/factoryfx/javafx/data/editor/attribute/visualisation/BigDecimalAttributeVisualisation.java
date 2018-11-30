package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.converter.BigDecimalStringConverter;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
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
