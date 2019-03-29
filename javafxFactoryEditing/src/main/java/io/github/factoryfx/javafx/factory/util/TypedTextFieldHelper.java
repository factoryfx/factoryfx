package io.github.factoryfx.javafx.factory.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.function.Consumer;

import com.google.common.base.Strings;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class TypedTextFieldHelper {


    private static void setupTextField(TextField textField, Consumer<String> converter) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            try {
                if (!Strings.isNullOrEmpty(change.getControlNewText())) {
                   converter.accept(change.getControlNewText());
                }
                return change;
            } catch (Exception e) {
                return null;
            }
        }));
    }

    public static void setupIntegerTextField(TextField textField) {
        setupTextField(textField, Integer::valueOf);
    }

    public static void setupShortTextField(TextField textField) {
        setupTextField(textField, Short::valueOf);
    }

    public static void setupURITextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                try {
                    URI uri = new URI(newValue);
                    textField.getStyleClass().removeIf("error"::equals);
                } catch (URISyntaxException e) {
                    textField.getStyleClass().add("error");
                }

            }
        });


    }

    public static void setupLocalDateTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).toFormatter(Locale.getDefault());
            if (newValue!=null){
                try {
                    dtf.parse(newValue);
                    textField.getStyleClass().removeIf("error"::equals);
                } catch (DateTimeParseException e) {
                    textField.getStyleClass().add("error");
                }

            }
        });


    }

    public static void setupLongTextField(TextField textField) {
        setupTextField(textField, Integer::valueOf);
    }

    public static void setupBigDecimalTextField(TextField textField, String decimalFormatPattern) {
        setupTextField(textField, (value) -> {
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatPattern);
            decimalFormat.setParseBigDecimal(true);
            ParsePosition pos = new ParsePosition(0);
            decimalFormat.parse(value,pos);
            if (pos.getIndex() != value.length() || pos.getErrorIndex() != -1) {
                throw new IllegalStateException();
            }
        });
    }

    public static void setupDoubleTextField(TextField textField) {
        setupTextField(textField, Double::valueOf);
    }

    static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;
    public static void setupLocalTimeTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                try {
                    TIME_FORMAT.parse(newValue);
                    textField.getStyleClass().removeIf("error"::equals);
                } catch (DateTimeParseException e) {
                    textField.getStyleClass().add("error");
                }

            }
        });
    }
}
