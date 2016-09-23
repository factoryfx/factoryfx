package de.factoryfx.javafx.util;

import com.google.common.base.Strings;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class TypedTextFieldHelper {

    public static void setupIntegerTextField(TextField textField) {
        textField.setTextFormatter(new TextFormatter<Integer>(change -> {
            try {
                if (!Strings.isNullOrEmpty(change.getControlNewText())) {
                    Integer.valueOf(change.getControlNewText());
                }
                return change;
            } catch (IllegalArgumentException e) { //javafxbug https://bugs.openjdk.java.net/browse/JDK-8081700
                return null;
            }
        }));
    }
}
