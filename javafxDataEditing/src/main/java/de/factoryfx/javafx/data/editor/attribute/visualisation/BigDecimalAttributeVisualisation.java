package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class BigDecimalAttributeVisualisation extends ValueAttributeEditorVisualisation<BigDecimal> {

    public final String decimalFormatPattern;

    public BigDecimalAttributeVisualisation(String decimalFormatPattern) {
        this.decimalFormatPattern = decimalFormatPattern;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<BigDecimal> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupBigDecimalTextField(textField,decimalFormatPattern);



        DecimalFormat decimalFormat = new DecimalFormat(decimalFormatPattern);
        decimalFormat.setParseBigDecimal(true);
        textField.textProperty().bindBidirectional(boundTo, new BigDecimalStringConverter(decimalFormat));
        textField.setEditable(!readonly);
        return textField;
    }

    public static class BigDecimalStringConverter extends StringConverter<BigDecimal> {
        private final DecimalFormat decimalFormat;

        public BigDecimalStringConverter(DecimalFormat decimalFormat) {
            this.decimalFormat = decimalFormat;
        }

        @Override
        public BigDecimal fromString(String value) {
            if (value == null) {
                return null;
            }

            value = value.trim();

            if (value.length() < 1) {
                return null;
            }

            try {
                return (BigDecimal) decimalFormat.parse(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString(BigDecimal value) {
            if (value == null) {
                return "";
            }
            return decimalFormat.format(value);
        }
    }
}
