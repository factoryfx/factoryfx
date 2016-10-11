package de.factoryfx.javafx.util;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.chrono.IsoChronology;
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

    private static Consumer<String> stringConsumer;

    private static void setupTextField(TextField textField, Consumer<String> converter) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            try {
                if (!Strings.isNullOrEmpty(change.getControlNewText())) {
                    converter.accept(change.getControlNewText());
                }
                return change;
            } catch (IllegalArgumentException e) { //javafxbug https://bugs.openjdk.java.net/browse/JDK-8081700
                return null;
            }
        }));
    }

    public static void setupIntegerTextField(TextField textField) {
        setupTextField(textField, Integer::valueOf);
    }

    public static void setupURITextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                try {
                    URI uri = new URI(newValue);
                    textField.getStyleClass().removeIf(c->"error".equals(c));
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
                    textField.getStyleClass().removeIf(c->"error".equals(c));
                } catch (DateTimeParseException e) {
                    textField.getStyleClass().add("error");
                }

            }
        });


    }

    public static void setupLongTextField(TextField textField) {
        setupTextField(textField, Integer::valueOf);
    }

    public static void setupBigDecimalLongTextField(TextField textField) {
        setupTextField(textField, BigDecimal::new);
    }

    public static void setupDoubleTextField(TextField textField) {
        setupTextField(textField, Double::valueOf);
    }

}
