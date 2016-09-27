package de.factoryfx.javafx.util;

import java.math.BigDecimal;
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
            } catch (IllegalArgumentException e) { //javafxbug https://bugs.openjdk.java.net/browse/JDK-8081700
                return null;
            }
        }));
    }

    public static void setupIntegerTextField(TextField textField) {
        setupTextField(textField, Integer::valueOf);
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
